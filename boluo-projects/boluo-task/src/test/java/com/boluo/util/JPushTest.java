package com.boluo.util;

import org.junit.Test;

import cn.jpush.api.JPushClient;
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
 * @since Jun 28, 2016
 */
public class JPushTest {
  private static final String MASTER_SECRET = "c37e6bcf18aa08c4dd05b07f";
  private static final String APP_KEY = "b5ca6cf2ee893f83a522f807";

  @Test
  public void testPush() throws Throwable {
    JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY);
    PushResult result = jpushClient.sendPush(PushPayload.newBuilder() //
        .setPlatform(Platform.ios()) //
        .setAudience(Audience.registrationId("1114a89792a94a03d76", "1517bfd3f7c6f8933f1"))
        .setNotification(Notification.newBuilder()
            .addPlatformNotification(IosNotification.newBuilder() //
                .setAlert("有人评论了你") //
                .setBadge(1).setSound("happy.caf").addExtra("from", "Dabllo").build())
            .build())
        .setMessage(Message.content("呵呵在 哈哈哈 中评论了你。")) //
        .setOptions(Options.newBuilder().setApnsProduction(false).build()) // 设置Push环境。
        .build());

    System.out.println(result.isResultOK());
  }

}
