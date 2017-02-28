package com.boluo.web.util;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @author mixueqiang
 * @since Aug 2, 2016
 */
public final class SmsUtilsYuntongxun {
  private static final Log LOG = LogFactory.getLog(SmsUtilsYuntongxun.class);
  // Setup Yuntongxun account information.
  private static final String ACCOUINT_SID = "";
  private static final String AUTH_TOKEN = "";
  private static final String APP_ID = "";
  private static final String URL = "https://app.cloopen.com:8883/2013-12-26/Accounts/" + ACCOUINT_SID + "/SMS/TemplateSMS?sig=";

  public static boolean send(String phone, int templateId, String[] datas) {
    JsonObject json = new JsonObject();
    json.addProperty("appId", APP_ID);
    json.addProperty("to", phone);
    json.addProperty("templateId", templateId);
    JsonArray jarray = new JsonArray();
    for (String data : datas) {
      jarray.add(new JsonPrimitive(data));
    }
    json.add("datas", jarray);
    String body = json.toString();

    try {
      BasicHttpEntity entity = new BasicHttpEntity();
      entity.setContentLength(body.getBytes("UTF-8").length);
      entity.setContent(new ByteArrayInputStream(body.getBytes("UTF-8")));

      String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
      String sigParameter = DigestUtils.md5Hex(ACCOUINT_SID + AUTH_TOKEN + date);
      HttpPost httpPost = new HttpPost(URL + sigParameter.toUpperCase());

      httpPost.setHeader("Accept", "application/json");
      httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
      String authorization = Base64.encodeBase64String(new String(ACCOUINT_SID + ":" + date).getBytes());
      httpPost.setHeader("Authorization", authorization);
      httpPost.setEntity(entity);

      HttpClient httpclient = HttpClients.createDefault();
      HttpResponse response = httpclient.execute(httpPost);

      System.out.println(EntityUtils.toString(response.getEntity()));
      return true;

    } catch (Throwable t) {
      LOG.info("Failed to send Sms through Yuntongxun.", t);
      return false;
    }
  }

}
