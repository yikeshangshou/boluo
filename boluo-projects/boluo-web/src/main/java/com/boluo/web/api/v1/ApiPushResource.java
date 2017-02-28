package com.boluo.web.api.v1;

import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

/**
 * @author mixueqiang
 * @since Jun 28, 2016
 */
@Path("/api/v1/push")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiPushResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(ApiPushResource.class);

  private static final String MASTER_SECRET = "c37e6bcf18aa08c4dd05b07f";
  private static final String APP_KEY = "b5ca6cf2ee893f83a522f807";

  @GET
  @Produces(APPLICATION_JSON)
  public Map<String, Object> send(@QueryParam("alert") String alert, @QueryParam("type") int type, @QueryParam("badge") @DefaultValue("1") int badge) {
    JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY);
    try {
      PushResult result = jpushClient.sendPush(PushPayload.newBuilder().setPlatform(Platform.ios()) //
          .setAudience(Audience.all())
          .setNotification(Notification.newBuilder()
              .addPlatformNotification(IosNotification.newBuilder() //
                  .setAlert(alert) //
                  .setBadge(1) //
                  .setSound("happy.caf").addExtra("type", type).build())
              .build())
          .setMessage(Message.content("哈哈哈在 呵呵呵 中评论了你。")).build());

      if (result.isResultOK()) {
        return ResponseBuilder.OK;
      }

      return ResponseBuilder.error(101, "发送失败！");

    } catch (Throwable t) {
      LOG.error("Failed to send push message!", t);
      return ResponseBuilder.error(101, t.getMessage());
    }
  }

}
