package com.boluo.notification.sms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.HtmlEmail;

import com.boluo.dao.EntityDao;
import com.boluo.model.Email;
import com.boluo.model.Entity;
import com.boluo.task.TaskScheduler;
import com.boluo.util.EmailTemplates;
import com.boluo.util.EmailUtils;

/**
 * @author mixueqiang
 * @since Jul 2, 2014
 */
public class SmsTaskUser implements Runnable {
  private static final Log LOG = LogFactory.getLog(SmsTaskUser.class);

  @Resource
  private EntityDao entityDao;

  public SmsTaskUser() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 30, 23);
  }

  @Override
  public void run() {
    try {
      Map<String, Object> condition = new HashMap<String, Object>();
      condition.put("status", 0);
      List<Entity> entities = entityDao.find("email", condition, 1, 5);
      if (CollectionUtils.isEmpty(entities)) {
        return;
      }

      LOG.info("Got " + entities.size() + " user emails.");

      for (Entity entity : entities) {
        LOG.info("Sending user email to: " + entity.getString("email"));

        // Send email.
        Map<String, Object> values = new HashMap<String, Object>();
        try {
          HtmlEmail email = null;
          if (entity.getInt("type") == Email.TYPE_USER_ACTIVATE) {
            email = EmailTemplates.getUserActivateEmail(entity.getString("toName"), entity.getString("email"), entity.getString("message"));

          } else if (entity.getInt("type") == Email.TYPE_WORKER_ACTIVATE) {
            email = EmailTemplates.getWorkerActivateEmail(entity.getString("toName"), entity.getString("email"), entity.getString("message"));

          } else if (entity.getInt("type") == Email.TYPE_COMPANY_ACTIVATE) {
            email = EmailTemplates.getCompanyActivateEmail(entity.getString("toName"), entity.getString("email"), entity.getString("message"));

          } else if (entity.getInt("type") == Email.TYPE_PASSWORD_RESET) {
            email = EmailTemplates.getPasswordResetEmail(entity.getString("email"), entity.getString("message"));

          } else {
            LOG.warn("Unknown email type for: " + entity);
            continue;
          }

          if (EmailUtils.send(email)) {
            values.put("status", 1);

          } else {
            values.put("status", -1);
          }

        } catch (Throwable t) {
          LOG.error("Error occurs on process email task!", t);
          values.put("status", -1);
        }

        values.put("sendTime", System.currentTimeMillis());
        entityDao.update("email", "id", entity.getLong("id"), values);
      }

    } catch (Throwable t) {
      LOG.error("Error occurs on processing user email tasks!", t);
    }
  }

}
