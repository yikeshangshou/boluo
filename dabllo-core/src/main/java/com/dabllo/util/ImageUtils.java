package com.dabllo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dabllo.Constants;
import com.dabllo.util.Pair;
import com.dabllo.util.RandomUtil;

/**
 * @author xueqiangmi
 * @since Jun 13, 2013
 */
public final class ImageUtils {
  private static final Log LOG = LogFactory.getLog(ImageUtils.class);

  public static final String SIZE_LARGE = "L";
  public static final String SIZE_MEDIUM = "M";
  public static final String SIZE_SMALL = "S";

  /**
   * Get the image and save it.
   */
  public static Pair<String, String> getAndSaveImage(String imageUrl) {
    try {
      URL url = new URL(imageUrl);
      InputStream inputStream = url.openConnection().getInputStream();
      return saveAndUpload(inputStream, StringUtils.substringAfterLast(url.getPath(), "/"));

    } catch (Throwable t) {
      LOG.error("Failed to get and save image: " + imageUrl, t);
      return null;
    }
  }

  public static String getImageUrl(String image) {
    return getImageUrl(image, ImageUtils.SIZE_MEDIUM);
  }

  public static String getImageUrl(String image, String sizeType) {
    if (StringUtils.isEmpty(image)) {
      return null;
    }

    if (StringUtils.startsWith(image, "http://") || StringUtils.startsWith(image, "https://")) {
      return image;

    } else {
      return UpYunUtils.URL + "/" + image + "!" + sizeType;
    }
  }

  /**
   * 保存到服务器本地文件系统。
   * 
   * @return 文件相对路径和文件名：Pair&lt;FilePath, FileName>
   */
  private static Pair<String, String> save(InputStream inputStream, String fileName) {
    if (StringUtils.isEmpty(fileName)) {
      return null;
    }

    File folder = null;
    String path = new SimpleDateFormat("yyyyMM").format(new Date());
    try {
      // 检查目录是否存在。
      folder = new File(Constants.IMAGE_REPO + path);
      if (!folder.exists()) {
        folder.mkdirs();
      }

    } catch (Throwable t) {
      LOG.error("Failed to create image folder on Web server!", t);
      return null;
    }

    boolean typeChecked = false; // 检查文件类型是否为图片
    // 保存文件。
    OutputStream out = null;
    try {
      int read = 0;
      byte[] bytes = new byte[1024];

      fileName = System.currentTimeMillis() + "_" + RandomUtil.randomString(10) + "." + StringUtils.substringAfterLast(fileName, ".");
      out = new FileOutputStream(new File(folder, fileName));
      while ((read = inputStream.read(bytes)) != -1) {
        if (!typeChecked) {
          if (!ImageTypeUtils.isImage(bytes)) {
            out.close();
            return null;
          }

          typeChecked = true;
        }

        out.write(bytes, 0, read);
      }

      out.flush();
      return new Pair<String, String>(path, fileName);

    } catch (Throwable t) {
      LOG.error("Failed to save image!", t);
      return null;

    } finally {
      try {
        if (out != null) {
          out.close();
        }
      } catch (Throwable t) {
        LOG.error("Failed to save image on Web server!", t);
      }
    }
  }

  public static Pair<String, String> saveAndUpload(InputStream inputStream, String fileName) {
    Pair<String, String> filePath = save(inputStream, fileName);
    if (filePath == null) {
      return null;
    }

    try {
      filePath = UpYunUtils.upload(filePath.left, filePath.right);
      if (filePath != null) {
        LOG.info("Upload image to image server OK: " + filePath);
        return filePath;
      }

    } catch (Throwable t) {
      LOG.error("Failed to upload image to image server!", t);
    }

    return null;
  }

}