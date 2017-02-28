package com.boluo.crawler.sync;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.boluo.dao.EntityDao;
import com.boluo.exception.ServiceException;
import com.boluo.model.Entity;
import com.boluo.model.Response;
import com.boluo.task.TaskScheduler;
import com.boluo.util.HttpClientUtils;
import com.boluo.util.ImageUtils;
import com.boluo.util.Pair;
import com.google.gson.Gson;

/**
 * @author mixueqiang
 * @since Sep 17, 2014
 */
public class InstagramFeedTask extends AbstractSyncTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(InstagramFeedTask.class);
  private static int PAGE_SIZE = 3;
  private static final String URL_FEED_POST = "http://www.aqwa.cn/api/v1/feed";

  public InstagramFeedTask() {
    TaskScheduler.register(getClass().getSimpleName(), this, 31, 1 * 60);
  }

  public InstagramFeedTask(EntityDao entityDao, int pageSize) {
    this.entityDao = entityDao;
    PAGE_SIZE = pageSize;
  }

  @Override
  public void finishTask(Entity source) {
    entityDao.update("source_feed", "id", source.getId(), "status", 1);
  }

  @Override
  public List<Entity> getTasks() {
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("status", 0);
    List<Entity> sources = entityDao.find("source_feed", condition, 1, PAGE_SIZE);
    return sources;
  }

  @Override
  public int processTask(Entity source) throws Throwable {
    String url = source.getString("url");
    long topicId = source.getLong("topicId");

    Document document = Jsoup.parse(new URL(url), 3000);
    Elements elements = document.getElementsByTag("script");
    for (Element element : elements) {
      if (element.hasAttr("src")) {
        continue;
      }

      String html = element.html();
      if (!StringUtils.startsWith(html, "window._sharedData")) {
        continue;
      }

      String title = StringUtils.substringBetween(html, "\"caption\": \"", "\",");
      title = StringEscapeUtils.unescapeJava(title);
      if (StringUtils.isEmpty(title)) {
        title = "最新资讯";

      } else if (StringUtils.length(title) > 200) {
        title = StringUtils.substring(title, 0, 197) + "...";
      }

      String image = StringUtils.substringBetween(html, "\"display_src\": \"", "\",");
      String path = null;
      try {
        Pair<String, String> imagePath = ImageUtils.getAndSaveImage(image);
        if (imagePath != null) {
          path = imagePath.left + "/" + imagePath.right;
        }

      } catch (Throwable t) {
        LOG.error("Failed to get image of feed: " + url, t);
        return -1;
      }

      // Save feed.
      Map<String, String> feed = new HashMap<String, String>();
      feed.put("category", "all");
      feed.put("topicId", topicId + "");
      feed.put("source", "Instagram");
      feed.put("title", title);
      feed.put("link", url);
      if (path != null) {
        feed.put("image", path);
      }
      feed.put("hidden", "0");
      feed.put("status", "1");
      String result = HttpClientUtils.post(URL_FEED_POST, feed);
      Response response = new Gson().fromJson(result, Response.class);
      if (response.getE() == 0) {
        LOG.info("Save a new feed: " + url);
        return 1;

      } else if (response.getE() == 10001) {
        LOG.warn("Cookie expires, signin again now...");
        if (!HttpClientUtils.signin(URL_SIGNIN, PARAMETERS)) {
          throw new ServiceException("Failed to signin Dabllo server!");
        }
        return 0;

      } else {
        LOG.warn("Failed to get feed: " + result);
      }
    }

    return -1;
  }

  @Override
  public void skipTask(Entity source) {
    entityDao.update("source_feed", "id", source.getId(), "status", -1);
  }

}
