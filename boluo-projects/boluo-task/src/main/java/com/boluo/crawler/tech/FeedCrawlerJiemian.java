package com.boluo.crawler.tech;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.boluo.crawler.sync.AbstractSyncTask;
import com.boluo.dao.EntityDao;
import com.boluo.model.Entity;
import com.boluo.task.TaskScheduler;

/**
 * @author mixueqiang
 * @since Sep 17, 2014
 */
@Service
public class FeedCrawlerJiemian extends AbstractSyncTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(FeedCrawlerJiemian.class);

  private static final String URL_FEED = "http://www.jiemian.com/lists/6.html";

  public FeedCrawlerJiemian() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 83, 20 * 60);
  }

  public FeedCrawlerJiemian(EntityDao entityDao) {
    this.entityDao = entityDao;
  }

  @Override
  public void finishTask(Entity source) {
    return;
  }

  @Override
  public List<Entity> getTasks() {
    List<Entity> sources = new ArrayList<Entity>();
    Entity entity = new Entity();
    entity.set("source", "Jiemian").set("topicId", 105);
    entity.set("url", URL_FEED);
    sources.add(entity);

    return sources;
  }

  @Override
  public int processTask(Entity source) throws Throwable {
    String url = source.getString("url");
    long topicId = source.getLong("topicId");

    try {
      Document document = Jsoup.parse(new URL(url), 5000);
      long time = System.currentTimeMillis();

      // 大公司头条
      Elements elements = document.select(".list-view .news-view");
      for (Element element : elements) {
        String imageUrl = element.select(".news-img img").attr("src");
        String image = getAndSaveImage(imageUrl);

        Element titleElement = element.select(".news-right .news-header a").first();
        String title = titleElement.text();
        String link = titleElement.absUrl("href");

        if (entityDao != null && !entityDao.exists("feed", "link", link)) {
          saveFeed(100002, topicId, title, link, "", source.getString("source"), image, time);
        }
      }

    } catch (Throwable t) {
      LOG.error("Failed to get feed: " + url, t);
      return -1;
    }

    return -1;
  }

  @Override
  public void skipTask(Entity source) {
    return;
  }

}
