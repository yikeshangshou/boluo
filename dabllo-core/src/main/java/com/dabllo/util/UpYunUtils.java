package com.dabllo.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dabllo.Constants;
import com.upyun.UpYun;

/**
 * @author xueqiangmi
 * @since Aug 10, 2013
 */
public final class UpYunUtils {
  private static final Log LOG = LogFactory.getLog(UpYunUtils.class);

  public static final String BUCKET_NAME = "wfenxiang";
  public static final String URL = "http://" + BUCKET_NAME + ".b0.upaiyun.com";
  private static final String USER_NAME = "mxq";
  private static final String USER_PWD = "SJTU1070379103";
  private static final String DIR_ROOT = "/";

  private static UpYun upyun = new UpYun(BUCKET_NAME, USER_NAME, USER_PWD);

  public static Pair<String, String> upload(File file) {
    if (file == null) {
      throw new NullPointerException();
    }

    String filePath = new SimpleDateFormat("yyyyMM").format(new Date());
    String fileName = System.currentTimeMillis() + "_" + RandomUtil.randomString(10) + "." + StringUtils.substringAfterLast(file.getName(), ".");
    return upload(filePath, fileName, file);
  }

  public static Pair<String, String> upload(String filePath, File file) {
    if (file == null) {
      throw new NullPointerException();
    }

    String fileName = System.currentTimeMillis() + "_" + RandomUtil.randomString(10) + "." + StringUtils.substringAfterLast(file.getName(), ".");
    return upload(filePath, fileName, file);
  }

  /**
   * 
   * @param filePath
   *          上传到的文件目录
   * @param fileName
   *          上传后的文件名
   * @param file
   *          上传的文件
   * @return 文件相对路径和文件名：Pair&lt;FilePath, FileName>
   */
  public static Pair<String, String> upload(String filePath, String fileName, File file) {
    if (file == null) {
      throw new NullPointerException();
    }

    String remoteFilePath = DIR_ROOT + filePath + File.separator + fileName;
    try {
      upyun.setContentMD5(UpYun.md5(file));
      if (upyun.writeFile(remoteFilePath, file, true)) {
        return new Pair<String, String>(filePath, fileName);
      }

    } catch (Throwable t) {
      LOG.error("Upload to upyun server error!", t);
    }

    return null;
  }

  /**
   * @param filePath
   *          文件目录：既是本地相对目录，也作为上传后的相对目录
   * @param fileName
   *          文件名：即是本地文件名，也作为上传后的文件名
   * @return 文件相对路径和文件名：Pair&lt;FilePath, FileName>
   */
  public static Pair<String, String> upload(String filePath, String fileName) {
    final File file = new File(Constants.IMAGE_REPO + filePath, fileName);
    if (!file.exists()) {
      throw new NullPointerException();
    }

    return upload(filePath, fileName, file);
  }

}
