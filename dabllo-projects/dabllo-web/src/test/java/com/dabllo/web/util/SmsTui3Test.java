package com.dabllo.web.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

/**
 * @author mixueqiang
 * @since Aug 1, 2016
 */
public class SmsTui3Test {

  @Test
  public void testSend() throws Throwable {
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("k", "965ec0048a207b1346a8652c40d8f077"));
    params.add(new BasicNameValuePair("r", "json"));
    params.add(new BasicNameValuePair("p", "1"));
    params.add(new BasicNameValuePair("t", "18668090654"));
    String content = "【大菠萝】验证码：" + "123456" + "。";
    content += "您正在大菠萝进行安全验证操作。如非本人操作，请忽略本条短信。";
    params.add(new BasicNameValuePair("c", content));

    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Consts.UTF_8);
    HttpPost httpPost = new HttpPost("http://tui3.com/api/send/");
    httpPost.setEntity(entity);

    HttpClient httpClient = HttpClients.createDefault();
    HttpResponse response = httpClient.execute(httpPost);
    if (response.getStatusLine().getStatusCode() == 200) {
      System.out.println("发送成功！");

    } else {
      System.out.println("发送失败！");
    }
  }

}
