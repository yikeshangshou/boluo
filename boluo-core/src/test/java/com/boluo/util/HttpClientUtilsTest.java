package com.boluo.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import com.boluo.util.HttpClientUtils;

/**
 * @author mixueqiang
 * @since Jun 24, 2016
 */
public class HttpClientUtilsTest {

  @Test
  public void test() {
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("phone", "18668090654");
    String password = "S1070379103";
    for (int i = 0; i < 5; i++) {
      password = DigestUtils.md5Hex(password);
    }
    parameters.put("password", password);

    HttpClientUtils.signin("http://www.aqwa.cn/signin", parameters);
  }

}
