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
 * @since Jul 29, 2016
 */
@Service
public class FeedCrawlerQQ extends AbstractSyncTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(FeedCrawlerQQ.class);

  private static final String URL_FEED = "http://tech.qq.com/";

  public FeedCrawlerQQ() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 31, 60);
  }

  public FeedCrawlerQQ(EntityDao entityDao) {
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
    entity.set("source", "QQ").set("topicId", 105);
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

      // 最新新闻
      Elements elements = document.select("#listZone .Q-tpList .Q-tpListInner");
      for (Element element : elements) {
        Element titleElement = element.select(".itemtxt a").first();
        String title = titleElement.text();
        String link = titleElement.absUrl("href");
        String description = null;

        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
        if (!StringUtils.contains(link, today)) {
          continue;
        }

        Element imageElement = element.select(".pic img").first();
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
