package com.dabllo.crawler.tech;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 * @since Jul 29, 2016
 */
public class FeedCrawlerBI extends AbstractSyncTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(FeedCrawlerBI.class);

  private static final String URL_FEED = "http://bi.qq.com/";

  public FeedCrawlerBI() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 31, 60);
  }

  public FeedCrawlerBI(EntityDao entityDao) {
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
    entity.set("source", "BI").set("topicId", 105);
    entity.set("url", URL_FEED);
    sources.add(entity);

    return sources;
  }

  @Override
  public int processTask(Entity source) throws Throwable {
    String url = source.getString("url");
    long topicId = source.getLong("topicId");
    String today = new SimpleDateFormat("yyyyMMdd").format(new Date());

    try {
      Document document = Jsoup.parse(new URL(url), 5000);
      long time = System.currentTimeMillis();

      // 头条
      Elements elements = document.select("#hotnews .zt-tabxxxx-boxx .mainnews");
      for (Element element : elements) {
        Element titleElement = element.select(".txt h2 a").first();
        String title = titleElement.text();
        String link = titleElement.absUrl("href");
        String description = element.select(".baizhu").text();

        if (!StringUtils.contains(link, today)) {
          continue;
        }

        Element imageElement = element.select(".dibg img").first();
        if (entityDao != null && !entityDao.exists("feed", "link", link)) {
          String imageUrl = imageElement.attr("src");
          String image = getAndSaveImage(imageUrl);

          saveFeed(100002, topicId, title, link, description, source.getString("source"), image, time);
        }
      }

      elements = document.select("#hotnews .zt-tabxxxx-boxx #listZone2 .Q-tpList .Q-tpListInner");
      for (Element element : elements) {
        Element titleElement = element.select("h3 a").first();
        String title = titleElement.text();
        String link = titleElement.absUrl("href");
        String description = element.select("p").text();

        if (!StringUtils.contains(link, today)) {
          continue;
        }

        Element imageElement = element.select("a img").first();
        if (entityDao != null && !entityDao.exists("feed", "link", link)) {
          String imageUrl = imageElement.attr("src");
          String image = getAndSaveImage(imageUrl);

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
