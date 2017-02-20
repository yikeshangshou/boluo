package com.dabllo.notification.push;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dabllo.model.Entity;
import com.dabllo.task.TaskScheduler;

import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

/**
 * @author mixueqiang
 * @since Jun 30, 2016
 */
@Service
public class PushTaskDaily extends AbstractPushTask {

  public PushTaskDaily() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 23, 30);
  }

  @Override
  public int getBusinessType() {
    return 10000;
  }

  @Override
  public String getPushContent(Entity entity) throws Exception {

    return entity.getString("content");
  }
  
  @Override
	public List<String> getRegistrationIds(Entity entity) throws Exception {
		return null;
	}

  @Override
  public String getTaskTableName() {
    return "notification_text";
  }
  
  @Override
  protected PushResult sendPush(List<String> registrationId, String content, int type, long dataId) throws Exception {
	  PushPayload payload = PushPayload.newBuilder() //
		        .setPlatform(Platform.ios()) //
		        .setAudience(Audience.all()) //
		        .setNotification(Notification.newBuilder()
		            .addPlatformNotification(IosNotification.newBuilder() //
		                .setAlert(content) //
		                .setBadge(1).setSound("happy.caf") //
		                .addExtra("jumpType", type).addExtra("jumpObjectId", dataId).build())
		            .build()) //
		        .setMessage(Message.content(content)) //
		        .setOptions(Options.newBuilder().setApnsProduction(true).build()) //
		        .build();

		    PushResult result = JPUSH_CLIENT.sendPush(payload);
	  return result;
  }

}
