package com.boluo.crawler.tech;

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
public class FeedCrawlerSina extends AbstractSyncTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(FeedCrawlerSina.class);

  private static final String URL_FEED = "http://tech.sina.com.cn/";

  public FeedCrawlerSina() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 31, 60);
  }

  public FeedCrawlerSina(EntityDao entityDao) {
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
    entity.set("source", "Sina").set("topicId", 105);
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

      // 重大新闻
      Elements elements = document.select("#impNews1 ul li a");
      for (Element element : elements) {
        String title = element.text();
        String link = element.absUrl("href");

        if (entityDao != null && !entityDao.exists("feed", "link", link)) {
          saveFeed(100002, topicId, title, link, "", source.getString("source"), "", time);
        }
      }

      // 最新新闻
      elements = document.select(".news-item");
      for (Element element : elements) {
        Element titleElement = element.select("h2 a").first();
        String title = titleElement.text();
        String link = titleElement.absUrl("href");
        String description = element.select(".txt a").first().text();

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (!StringUtils.contains(link, today)) {
          continue;
        }

        if (entityDao != null && !entityDao.exists("feed", "link", link)) {
          String imageUrl = element.select(".img img").attr("src");
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
