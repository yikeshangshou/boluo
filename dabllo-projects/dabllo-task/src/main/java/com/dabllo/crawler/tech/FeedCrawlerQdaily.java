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

import com.dabllo.crawler.sync.AbstractSyncTask;
import com.dabllo.dao.EntityDao;
import com.dabllo.model.Entity;
import com.dabllo.task.TaskScheduler;

/**
 * @author mixueqiang
 * @since Sep 17, 2014
 */
public class FeedCrawlerQdaily extends AbstractSyncTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(FeedCrawlerQdaily.class);

  private static final String URL_FEED = "http://www.qdaily.com/tags/7294.html";

  public FeedCrawlerQdaily() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 79, 60 * 60);
  }

  public FeedCrawlerQdaily(EntityDao entityDao) {
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
    entity.set("source", "Qdaily").set("topicId", 105);
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
      Elements elements = document.select(".page-content .articles .article a");
      for (Element element : elements) {
        Element titleElement = element.select(".grid-article-bd h3").first();
        String title = titleElement.text();
        title = StringUtils.substringAfter(title, "大公司头条：");
        String link = element.absUrl("href");

        if (entityDao != null && !entityDao.exists("feed", "link", link)) {
          saveFeed(100002, topicId, title, link, "", source.getString("source"), "", time);
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
