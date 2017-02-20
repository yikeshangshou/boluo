package com.dabllo.crawler.tech;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dabllo.crawler.sync.AbstractSyncTask;
import com.dabllo.dao.EntityDao;
import com.dabllo.model.Entity;
import com.dabllo.task.TaskScheduler;

/**
 * @author mixueqiang
 * @since Jul 6, 2016
 */
public class FeedCrawlerGeekpark extends AbstractSyncTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(FeedCrawlerGeekpark.class);
  private static final String URL_FEED = "http://www.geekpark.net/news_list?page=1";

  public FeedCrawlerGeekpark() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 41, 60);
  }

  public FeedCrawlerGeekpark(EntityDao entityDao) {
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
    entity.set("source", "Geekpark").set("topicId", 104);
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

      // 快闻
      Elements elements = document.select(".news-item");
      for (Element element : elements) {
        String title = element.select(".news-title").first().text();

        Elements sourceElements = element.select(".news-source");
        if (sourceElements == null || sourceElements.first() == null) {
          continue;
        }
        String link = element.select(".news-source").first().absUrl("href");

        String description = null;
        Elements descElements = element.select(".news-content");
        if (descElements != null && descElements.first() != null) {
          description = descElements.first().text();
        }

        if (entityDao != null && !entityDao.exists("feed", "link", link)) {
          saveFeed(100002, topicId, title, link, description, source.getString("source"), "", time);
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
