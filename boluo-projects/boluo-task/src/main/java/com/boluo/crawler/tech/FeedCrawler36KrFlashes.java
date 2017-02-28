package com.boluo.crawler.tech;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.boluo.crawler.sync.AbstractSyncTask;
import com.boluo.crawler.tech.Response.Item;
import com.boluo.dao.EntityDao;
import com.boluo.model.Entity;
import com.boluo.task.TaskScheduler;
import com.boluo.util.HttpClientUtils;
import com.google.gson.Gson;

/**
 * @author mixueqiang
 * @since Sep 17, 2014
 */
public class FeedCrawler36KrFlashes extends AbstractSyncTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(FeedCrawler36KrFlashes.class);

  private static int PAGE_SIZE = 20;
  private static final String URL_FEED = "http://36kr.com/api/newsflash?b_id={0}&per_page={1}&_={2}";

  public FeedCrawler36KrFlashes() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 23, 60);
  }

  public FeedCrawler36KrFlashes(EntityDao entityDao, int pageSize) {
    this.entityDao = entityDao;
    PAGE_SIZE = pageSize;
  }

  @Override
  public void finishTask(Entity source) {
    return;
  }

  @Override
  public List<Entity> getTasks() {
    List<Entity> sources = new ArrayList<Entity>();
    Entity entity = new Entity();
    entity.set("source", "36Kr").set("topicId", 103);
    String url = MessageFormat.format(URL_FEED, new Object[] { 0, PAGE_SIZE, String.valueOf(System.currentTimeMillis()) });
    entity.set("url", url);
    sources.add(entity);

    return sources;
  }

  @Override
  public int processTask(Entity source) throws Throwable {
    String url = source.getString("url");
    long topicId = source.getLong("topicId");

    try {
      String json = HttpClientUtils.get(url);
      Response response = new Gson().fromJson(json, Response.class);
      long time = System.currentTimeMillis();

      for (Item item : response.getData().getItems()) {
        String link = item.getNews_url();

        if (entityDao != null && !entityDao.exists("feed", "link", link)) {
          String title = item.getTitle();
          String description = item.getDescription();
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
