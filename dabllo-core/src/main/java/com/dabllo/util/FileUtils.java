package com.dabllo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mixueqiang
 * @since Mar 10, 2014
 */
public final class FileUtils {
  private static final Log LOG = LogFactory.getLog(FileUtils.class);
  public static final String FILE_REPOSITORY = "/data/transkip/";
  public static final int FILE_MAX_SIZE = 2 * 1024 * 1024; // 2MB.

  /**
   * Save the file.
   */
  public static String save(InputStream inputStream, String fileName) {
    // Generate folder and file name.
    Date now = CalendarUtils.getCalendar().getTime();
    String folder = new SimpleDateFormat("yyyyMM").format(now) + "/";
    String time = String.valueOf(now.getTime());
    String newFileName = time + "_" + RandomUtil.randomString(6);
    if (StringUtils.contains(fileName, ".")) {
      newFileName += "." + StringUtils.substringAfterLast(fileName, ".");
    }

    // Check folder.
    File directory = null;
    try {
      directory = new File(FILE_REPOSITORY + folder);
      if (!directory.exists()) {
        directory.mkdirs();
      }

    } catch (Throwable t) {
      LOG.error("Failed to make file directory!", t);
      return null;
    }

    // Save file.
    OutputStream out = null;
    try {
      int read = 0;
      int fileSize = 0;
      byte[] bytes = new byte[4 * 1024];

      out = new FileOutputStream(new File(directory, newFileName));
      while ((read = inputStream.read(bytes)) != -1) {
        out.write(bytes, 0, read);
        fileSize += 4 * 1024;

        if (fileSize > FILE_MAX_SIZE) {
          out.close();
          throw new IllegalArgumentException("File is too large!");
        }
      }

      out.flush();
      return folder + newFileName;

    } catch (Throwable t) {
      LOG.error("Failed to save file!", t);
      return null;

    } finally {
      try {
        if (out != null) {
          out.close();
        }
      } catch (Throwable t) {
        LOG.error("Failed to save file!", t);
      }
    }
  }

}