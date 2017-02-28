package com.boluo.util;

import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

import com.boluo.model.Response;
import com.google.gson.Gson;

public class HttpClientUtils {

  public static final String CHARSET = "UTF-8";
  public static final int CONNECTION_TIMEOUT = 10000;
  public static final int READ_TIME = 10000;

  private static HttpClient client = null;
  private static final String URL_SIGNIN = "http://www.aqwa.cn/api/v1/user/signin";
  private static final String PHONE = "10000000000";
  private static final String PASSWORD = "S1070379103";
  private static final CookieStore cookieStore = new BasicCookieStore();

  static {
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    cm.setMaxTotal(128);
    cm.setDefaultMaxPerRoute(128);
    client = HttpClients.custom().setDefaultCookieStore(cookieStore).setConnectionManager(cm).build();
  }

  public static boolean signin() {
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("phone", PHONE);
    String password = PASSWORD;
    for (int i = 0; i < 5; i++) {
      password = DigestUtils.md5Hex(password);
    }
    parameters.put("password", password);

    return HttpClientUtils.signin(URL_SIGNIN, parameters);
  }

  public static boolean signin(String url, Map<String, String> parameters) {
    try {
      String json = post(url, parameters);
      Response response = new Gson().fromJson(json, Response.class);
      if (response.getE() == 0) {
        for (Cookie cookie : cookieStore.getCookies()) {
          if (StringUtils.equals(cookie.getName(), "JSESSIONID")) {
            return true;
          }
        }
      }

      return false;

    } catch (Throwable e) {
      e.printStackTrace();
    }

    return false;
  }

  public static String get(String url) throws Exception {
    return get(url, CHARSET, null, null);
  }

  public static String get(String url, String charset) throws Exception {
    return get(url, charset, CONNECTION_TIMEOUT, READ_TIME);
  }

  /**
   * 发送一个 GET 请求
   */
  public static String get(String url, String charset, Integer connTimeout, Integer readTimeout) throws ConnectTimeoutException, SocketTimeoutException, Exception {
    HttpClient client = null;
    HttpGet get = new HttpGet(url);
    String result = "";
    try {
      // 设置参数
      Builder customReqConf = RequestConfig.custom();
      if (connTimeout != null) {
        customReqConf.setConnectTimeout(connTimeout);
      }
      if (readTimeout != null) {
        customReqConf.setSocketTimeout(readTimeout);
      }
      get.setConfig(customReqConf.build());

      HttpResponse res = null;

      if (url.startsWith("https")) {
        // 执行 Https 请求.
        client = createSSLInsecureClient();
        res = client.execute(get);
      } else {
        // 执行 Http 请求.
        client = HttpClientUtils.client;
        res = client.execute(get);
      }

      result = IOUtils.toString(res.getEntity().getContent(), charset);
    } finally {
      get.releaseConnection();
      if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient) {
        ((CloseableHttpClient) client).close();
      }
    }
    return result;
  }

  public static String post(String url, Map<String, String> parameters) throws Throwable {
    return post(url, parameters, null, CONNECTION_TIMEOUT, READ_TIME);
  }

  public static String post(String url, Map<String, String> parameters, Integer connTimeout, Integer readTimeout) throws Throwable {
    return post(url, parameters, null, connTimeout, readTimeout);
  }

  /**
   * 提交form表单
   */
  public static String post(String url, Map<String, String> parameters, Map<String, String> headers, Integer connTimeout, Integer readTimeout) throws Throwable {
    HttpClient client = null;
    HttpPost post = new HttpPost(url);
    try {
      if (parameters != null && !parameters.isEmpty()) {
        List<NameValuePair> formParams = new ArrayList<org.apache.http.NameValuePair>();
        Set<Entry<String, String>> entrySet = parameters.entrySet();
        for (Entry<String, String> entry : entrySet) {
          formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
        post.setEntity(entity);
      }

      if (headers != null && !headers.isEmpty()) {
        for (Entry<String, String> entry : headers.entrySet()) {
          post.addHeader(entry.getKey(), entry.getValue());
        }
      }
      // 设置参数
      Builder customReqConf = RequestConfig.custom();
      if (connTimeout != null) {
        customReqConf.setConnectTimeout(connTimeout);
      }
      if (readTimeout != null) {
        customReqConf.setSocketTimeout(readTimeout);
      }
      post.setConfig(customReqConf.build());
      HttpResponse httpResponse = null;
      if (url.startsWith("https")) {
        // 执行 Https 请求.
        client = createSSLInsecureClient();
        httpResponse = client.execute(post);
      } else {
        // 执行 Http 请求.
        client = HttpClientUtils.client;
        httpResponse = client.execute(post);
      }

      return IOUtils.toString(httpResponse.getEntity().getContent(), "UTF-8");

    } finally {
      post.releaseConnection();
      if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient) {
        ((CloseableHttpClient) client).close();
      }
    }
  }

  public static String post(String url, String parameters) throws Throwable {
    return post(url, parameters, "application/x-www-form-urlencoded", CHARSET, CONNECTION_TIMEOUT, READ_TIME);
  }

  public static String post(String url, String parameters, String charset, Integer connTimeout, Integer readTimeout) throws Throwable {
    return post(url, parameters, "application/x-www-form-urlencoded", charset, connTimeout, readTimeout);
  }

  /**
   * 发送一个 Post 请求, 使用指定的字符集编码.
   * 
   * <pre>
   * String response = post("https://localhost:443/ssl/test.shtml", "name=12&page=34", "application/x-www-form-urlencoded", "UTF-8", 10000, 10000);
   * </pre>
   * 
   */
  public static String post(String url, String body, String mimeType, String charset, Integer connTimeout, Integer readTimeout) throws Throwable {
    HttpClient client = null;
    HttpPost post = new HttpPost(url);
    String result = "";
    try {
      if (StringUtils.isNotBlank(body)) {
        HttpEntity entity = new StringEntity(body, ContentType.create(mimeType, charset));
        post.setEntity(entity);
      }
      // 设置参数
      Builder customReqConf = RequestConfig.custom();
      if (connTimeout != null) {
        customReqConf.setConnectTimeout(connTimeout);
      }
      if (readTimeout != null) {
        customReqConf.setSocketTimeout(readTimeout);
      }
      post.setConfig(customReqConf.build());

      HttpResponse httpResponse;
      if (url.startsWith("https")) {
        // 执行 Https 请求.
        client = createSSLInsecureClient();
        httpResponse = client.execute(post);
      } else {
        // 执行 Http 请求.
        client = HttpClientUtils.client;
        httpResponse = client.execute(post);
      }

      result = IOUtils.toString(httpResponse.getEntity().getContent(), charset);

    } finally {
      post.releaseConnection();
      if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient) {
        ((CloseableHttpClient) client).close();
      }
    }
    return result;
  }

  /**
   * 创建 SSL连接
   */
  private static CloseableHttpClient createSSLInsecureClient() throws GeneralSecurityException {
    try {
      SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
          return true;
        }
      }).build();

      SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
      return HttpClients.custom().setSSLSocketFactory(sslsf).build();

    } catch (GeneralSecurityException e) {
      throw e;
    }
  }

  /**
   * 从 response 里获取 Charset.
   */
  @SuppressWarnings("unused")
  private static String getCharsetFromResponse(HttpResponse ressponse) {
    // Content-Type:text/html; charset=GBK
    if (ressponse.getEntity() != null && ressponse.getEntity().getContentType() != null && ressponse.getEntity().getContentType().getValue() != null) {
      String contentType = ressponse.getEntity().getContentType().getValue();
      if (contentType.contains("charset=")) {
        return contentType.substring(contentType.indexOf("charset=") + 8);
      }
    }
    return null;
  }

}