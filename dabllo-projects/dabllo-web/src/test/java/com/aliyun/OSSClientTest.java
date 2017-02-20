package com.aliyun;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.dabllo.util.RandomUtil;

/**
 * @author mixueqiang
 * @since Jan 24, 2016
 */
public class OSSClientTest {
  private static final String ENDPOINT = "http://oss-cn-hangzhou.aliyuncs.com";
  private static final String ACCESS_KEY_ID = "EUxn0lNLr58Zn9QF";
  private static final String ACCESS_KEY_SECRET = "OZZzS3S2cIWpOXVEOWqsUjbtUNIBB2";

  @Test
  public void test() throws Throwable {
    ClientConfiguration configuration = new ClientConfiguration();
    configuration.setMaxConnections(10);
    configuration.setConnectionTimeout(3000);
    configuration.setMaxErrorRetry(3);
    configuration.setSocketTimeout(1000);
    OSSClient client = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET, configuration);

    String key = System.currentTimeMillis() + "_" + RandomUtil.randomString(10);
    File file = new File("/Users/mixueqiang/Pictures/b_002.jpg");
    InputStream inputstream = new FileInputStream(file);

    ObjectMetadata metadata = new ObjectMetadata();
    String contentType = "image/";
    String fileType = StringUtils.substringAfterLast(file.getName(), ".");
    if (StringUtils.equals(fileType, "png")) {
      contentType += "png";
    } else if (StringUtils.equals(fileType, "gif")) {
      contentType += "gif";
    } else {
      contentType += "jpeg";
    }
    metadata.setContentType(contentType);

    client.putObject("wfenxiang", key, inputstream, metadata);

    client.shutdown();
  }

}
