package com.dabllo.web.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

/**
 * @author mixueqiang
 * @since Aug 2, 2016
 */
public final class SmsUtilsYunpian {
  private static final Log LOG = LogFactory.getLog(SmsUtilsYunpian.class);
  private static final String APP_KEY = "8e90bfc96a9a9a143bc7e875ac4392a7";
  private static final String URL = "https://sms.yunpian.com/v2/sms/single_send.json";
  private static final Map<String, String> TEMPLATES = new HashMap<String, String>();

  static {
    TEMPLATES.put("register", "【大菠萝】验证码：{0}。您正在大菠萝进行绑定手机操作。如非本人操作，请忽略本条短信。");
    TEMPLATES.put("reset_password", "【大菠萝】验证码：{1}。您正在大菠萝进行重置密码操作。如非本人操作，请忽略本条短信");
  }

  public static boolean send(String phone, String type, String[] datas) {
    try {
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("apikey", APP_KEY));
      params.add(new BasicNameValuePair("mobile", phone));
      params.add(new BasicNameValuePair("text", buildMessage(type, datas)));

      UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Consts.UTF_8);
      HttpPost httpPost = new HttpPost(URL);
      httpPost.setEntity(entity);

      HttpClient httpClient = HttpClients.createDefault();
      HttpResponse response = httpClient.execute(httpPost);
      String content = EntityUtils.toString(response.getEntity());
      if (response.getStatusLine().getStatusCode() == 200) {
        YunpianResult result = new Gson().fromJson(content, YunpianResult.class);
        if (result.getCode() == 0) {
          return true;

        } else {
          LOG.error("Failed to send Sms: " + content);
          return true;
        }

      } else {
        LOG.error("Failed to send Sms: " + content);
        return false;
      }

    } catch (Throwable t) {
      LOG.info("Failed to send Sms through Yunpian.", t);
    }

    return false;
  }

  private static String buildMessage(String type, Object[] datas) throws Exception {
    String template = TEMPLATES.get(type);
    if (StringUtils.isEmpty(template)) {
      throw new Exception("Unknown sms type: " + type);
    }

    String message = MessageFormat.format(template, datas);
    return message;
  }

}

class YunpianResult {
  int code;
  int count;
  double fee;
  String mobile;
  String msg;
  long sid;
  String unit;

  public int getCode() {
    return code;
  }

  public int getCount() {
    return count;
  }

  public double getFee() {
    return fee;
  }

  public String getMobile() {
    return mobile;
  }

  public String getMsg() {
    return msg;
  }

  public long getSid() {
    return sid;
  }

  public String getUnit() {
    return unit;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public void setFee(double fee) {
    this.fee = fee;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public void setSid(long sid) {
    this.sid = sid;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

}