package com.boluo.web.api.v1;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.boluo.model.User;
import com.boluo.util.ImageUtils;
import com.boluo.util.Pair;
import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * @author xueqiangmi
 * @since Jul 3, 2015
 */
@Path("/api/v1/image")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiImageResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(ApiImageResource.class);

  @GET
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getAndSaveImage(@QueryParam("imageUrl") String imageUrl) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    try {
      Pair<String, String> imagePath = ImageUtils.getAndSaveImage(imageUrl);
      String path = imagePath.left + "/" + imagePath.right;
      LOG.info("File " + imageUrl + " saved to: " + path);

      Map<String, String> result = new HashMap<String, String>();
      result.put("path", path);
      result.put("url", ImageUtils.getImageUrl(path, "M"));
      return ResponseBuilder.ok(result);

    } catch (Throwable t) {
      LOG.warn("Error occurs on reading image.", t);
      return ResponseBuilder.error(50000, "获取图片失败。");
    }
  }

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> save(@FormDataParam("imageFile") InputStream inputStream, @FormDataParam("imageFile") FormDataContentDisposition fileDetails) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (inputStream == null || StringUtils.isEmpty(fileDetails.getFileName())) {
      return ResponseBuilder.error(101, "请上传一张图片。");
    }

    try {
      // Save an image.
      String fileName = fileDetails.getFileName();
      Pair<String, String> imagePath = ImageUtils.saveAndUpload(inputStream, fileName);
      String path = imagePath.left + "/" + imagePath.right;
      LOG.info("File " + fileName + " saved to: " + path);

      Map<String, String> result = new HashMap<String, String>();
      result.put("path", path);
      result.put("url", ImageUtils.getImageUrl(path, "M"));
      return ResponseBuilder.ok(result);

    } catch (Throwable t) {
      LOG.warn("Error occurs on saving image.", t);
      return ResponseBuilder.error(50000, "保存图片失败。");
    }
  }

}
