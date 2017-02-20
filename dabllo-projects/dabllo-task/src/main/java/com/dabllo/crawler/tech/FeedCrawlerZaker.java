package com.dabllo.crawler.tech;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.dabllo.crawler.sync.AbstractSyncTask;
import com.dabllo.dao.EntityDao;
import com.dabllo.model.Entity;
import com.dabllo.task.TaskScheduler;

/**
 * @author mixueqiang
 * @since Jul 22, 2016
 */
@Service
public class FeedCrawlerZaker extends AbstractSyncTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(FeedCrawlerZaker.class);

  private static final String URL_FEED = "http://app.myzaker.com/news/app.php?app_id=13&f=";

  public FeedCrawlerZaker() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 47, 60);
  }

  public FeedCrawlerZaker(EntityDao entityDao) {
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
    entity.set("source", "Zaker").set("topicId", 105);
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

      Elements elements = document.select("#article_list_id #infinite_scroll a");
      for (Element element : elements) {
        String link = element.absUrl("href");
        if (StringUtils.contains(link, "?discussion_id=") || StringUtils.contains(link, "?topic_id=")) {
          // 跳过话题和专题。
          continue;
        }

        Element titleElement = element.select(".cell .cell_left .title").first();
        String title = titleElement.text();
        String description = null;

        Element dateElement = element.select(".cell .cell_left .date").first();
        String date = dateElement.text();
        if (!StringUtils.contains(date, "分钟前") && !StringUtils.contains(date, "小时前")) {
          // 跳过昨天的资讯
          continue;
        }

        Element imageElement = element.select(".cell .pic-cover").first();
        if (entityDao != null && !entityDao.exists("feed", "link", link)) {
          String imageStyle = imageElement.attr("style");
          String imageUrl = StringUtils.substringBetween(imageStyle, "background-image:url(", ")");
          String image = null;
          if (StringUtils.isNotEmpty(imageUrl)) { // 抓取和保存图片。
            image = getAndSaveImage(imageUrl);
          }

          saveFeed(100002, topicId, title, link, description, source.getString("source"), image, time);
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
