package com.dabllo.crawler;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dabllo.dao.EntityDao;
import com.dabllo.exception.ServiceException;
import com.dabllo.model.Entity;
import com.dabllo.util.HttpClientUtils;
import com.dabllo.util.ImageUtils;
import com.dabllo.util.Pair;

/**
 * @author mixueqiang
 * @since Sep 17, 2014
 */
public abstract class AbstractFeedTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(AbstractFeedTask.class);
  private static final String PASSWORD = "S1070379103";
  private static final String PHONE = "10000000000";
  protected static final Map<String, String> PARAMETERS = new HashMap<String, String>();
  protected static final String URL_SIGNIN = "http://www.aqwa.cn/api/v1/user/signin";

  static {
    PARAMETERS.put("phone", PHONE);
    String password = PASSWORD;
    for (int i = 0; i < 5; i++) {
      password = DigestUtils.md5Hex(password);
    }
    PARAMETERS.put("password", password);

    if (!HttpClientUtils.signin(URL_SIGNIN, PARAMETERS)) {
      throw new ServiceException("Failed to sign in Dabllo server!");
    }
  }

  @Resource
  protected EntityDao entityDao;

  public abstract void finishTask(Entity source);

  public abstract List<Entity> getSources();

  public abstract int processTask(Entity source) throws Throwable;

  @Override
  public void run() {
    try {
      List<Entity> sources = getSources();
      if (CollectionUtils.isEmpty(sources)) {
        LOG.info("Get 0 sources on task: " + getClass().getSimpleName());
        return;
      }

      LOG.info("Get " + sources.size() + " sources on task: " + getClass().getSimpleName());

      for (Entity source : sources) {
        String url = source.getString("url");
        LOG.info("Process soruce " + url);

        try {
          switch (processTask(source)) {
          case 1:
            finishTask(source);
            break;
          case 0:
            // Retry the task next time.
            break;
          case -1:
            skipTask(source);
            break;
          }

        } catch (Throwable t) {
          LOG.error("Failed to process source: " + url, t);
        }
      }

    } catch (Throwable t) {
      LOG.error("Failed to process feed task!", t);
    }
  }

  public abstract void skipTask(Entity source);

  protected String getAndSaveImage(String imageUrl) {
    Pair<String, String> filePath = null;
    try {
      filePath = ImageUtils.getAndSaveImage(imageUrl);

    } catch (Throwable t) {
      LOG.warn("Failed to get image: " + imageUrl);
    }

    if (filePath != null) {
      return filePath.left + File.separator + filePath.right;

    } else {
      return imageUrl;
    }
  }

  protected void saveFeed(long userId, long topicId, String title, String link, String description, String source, String image, long time) {
    try {
      Entity entity = new Entity("feed");
      entity.set("userId", userId).set("topicId", topicId).set("title", title);
      entity.set("link", link).set("image", image).set("description", description).set("source", source);
      entity.set("status", 1).set("createTime", time);

      entityDao.save(entity);
      LOG.info("Feed saved done: " + source + " - " + link);

    } catch (Throwable t) {
      LOG.error("Failed to save feed: " + source + " - " + link, t);
    }
  }

}
