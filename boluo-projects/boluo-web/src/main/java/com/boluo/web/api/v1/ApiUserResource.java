package com.boluo.web.api.v1;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.boluo.Constants;
import com.boluo.dao.mapper.UserRowMapper;
import com.boluo.model.Entity;
import com.boluo.model.User;
import com.boluo.service.SessionService;
import com.boluo.util.EncryptUtils;
import com.boluo.util.Pair;
import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;

/**
 * @author mixueqiang
 * @since Apr 29, 2016
 */
@Path("/api/v1/user")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiUserResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(ApiUserResource.class);
  private static final String[] PROFILE_KEYS = new String[] { "email", "username", "avatar", "gender", "birthday", "city", "profile" };

  @Context
  protected HttpServletResponse response;
  @Resource
  protected SessionService sessionService;

  @GET
  @Path("{userId}")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> get(@PathParam("userId") long userId) {
    User user = entityDao.get("user", userId, UserRowMapper.getInstance());
    if (user == null) {
      return ResponseBuilder.error(10404, "未找到用户。");
    }

    Map<String, Object> userInfo = filterUser(user);
    return ResponseBuilder.ok(userInfo);
  }

  @GET
  @Path("profile")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getProfile() {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    Entity entity = entityDao.get("user", user.getId());
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("id", entity.getId());
    String phone = entity.getString("phone");
    if (StringUtils.isNotEmpty(phone)) {
      phone = StringUtils.substring(phone, 0, 3) + "****" + StringUtils.substring(phone, 7);
    }
    result.put("phone", phone);
    result.put("username", entity.getString("username"));
    result.put("bindStatus", entity.getInt("bindStatus"));
    result.put("avatar", entity.getString("avatar"));
    result.put("gender", entity.getString("gender"));
    result.put("birthday", entity.getString("birthday"));
    result.put("city", entity.getString("city"));
    result.put("profile", entity.getString("profile"));

    return ResponseBuilder.ok(result);
  }

  @GET
  @Path("stats")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getStats() {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    Map<String, Object> result = new HashMap<String, Object>();

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("userId", user.getId());
    condition.put("status", Constants.STATUS_ENABLED);

    // 讨论总数量。
    int discussionCount = entityDao.count("discussion", condition);
    result.put("discussionCount", discussionCount);

    // 观点总数量。
    int replyCount = entityDao.count("reply", condition);
    result.put("replyCount", replyCount);

    return ResponseBuilder.ok(result);
  }

  @POST
  @Path("bind_phone")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> bindPhone(@FormParam("deviceId") String deviceId, @FormParam("phone") String phone, @FormParam("security_code") String securityCode) {
    // Data validation.
    if (StringUtils.isEmpty(phone)) {
      return ResponseBuilder.error(10101, "请输入手机号。");
    }
    if (StringUtils.isNotEmpty(phone) && StringUtils.length(phone) != 11) {
      return ResponseBuilder.error(10102, "请输入有效的手机号。");
    }
    String sessionKey = phone + "_register";
    @SuppressWarnings("unchecked")
    Pair<Long, String> pair = (Pair<Long, String>) getSessionAttribute(sessionKey);
    if (pair == null || !StringUtils.equals(securityCode, pair.right)) {
      return ResponseBuilder.error(10105, "验证码错误。");
    }

    try {
      long time = System.currentTimeMillis();
      if (!entityDao.exists("user", "phone", phone)) {
        // 首次绑定，保存用户信息。
        Entity user = new Entity("user");
        user.set("phone", phone).set("deviceId", deviceId);
        user.set("email", "").set("username", "").set("password", "");
        user.set("locale", "cn").set("roles", "user").set("bindStatus", 1).set("status", 1).set("createTime", time);
        user = entityDao.saveAndReturn(user);

        // 记录用户新设备信息，该信息可能会重复记录。
        Entity userDevice = new Entity("user_device");
        userDevice.set("userId", user.getId()).set("deviceId", deviceId);
        userDevice.set("status", 1).set("createTime", time);
        entityDao.save(userDevice);
        LOG.info("New user binds phone: " + phone);

      } else {
        // 更新绑定的设备ID和绑定状态。
        Map<String, Object> updateValues = new HashMap<String, Object>();
        updateValues.put("deviceId", deviceId);
        updateValues.put("bindStatus", 1);
        updateValues.put("updateTime", time);
        entityDao.update("user", "phone", phone, updateValues);
        LOG.info("User binds phone: " + phone);
      }

      // 更新Session。
      HttpSession session = request.getSession(false);
      if (session != null) {
        session.invalidate();
      }
      User user = entityDao.findOne("user", "phone", phone, UserRowMapper.getInstance());
      WebUtils.setSessionAttribute(request, "_user", user);

      // Store session id。
      String sessionId = WebUtils.getSessionId(request);
      sessionService.storeSession(user.getId(), sessionId);

      setSessionAttribute("_user", user);
      return ResponseBuilder.ok(user.getUsername());

    } catch (Throwable t) {
      LOG.error("Error occurs when binding phone.", t);
      return ResponseBuilder.error(500, "绑定手机失败，请稍后再试!");
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> save(@FormParam("phone") String phone, @FormParam("securityCode") String securityCode, @FormParam("password") String password,
      @FormParam("confirmPassword") String confirmPassword) {
    // Data validation.
    if (StringUtils.isEmpty(phone)) {
      return ResponseBuilder.error(10101, "请输入手机号。");
    }
    if (StringUtils.isNotEmpty(phone) && StringUtils.length(phone) != 11) {
      return ResponseBuilder.error(10102, "请输入有效的手机号。");
    }
    if (StringUtils.isEmpty(password)) {
      return ResponseBuilder.error(10103, "请输入密码。");
    }
    if (StringUtils.isNotEmpty(confirmPassword) && !StringUtils.equals(password, confirmPassword)) {
      return ResponseBuilder.error(10104, "两次输入的密码不一致。");
    }
    String sessionKey = phone + "_register";
    @SuppressWarnings("unchecked")
    Pair<Long, String> pair = (Pair<Long, String>) getSessionAttribute(sessionKey);
    if (!StringUtils.equals(securityCode, pair.right)) {
      return ResponseBuilder.error(10105, "验证码错误。");
    }

    try {
      if (entityDao.exists("user", "phone", phone)) {
        return ResponseBuilder.error(10106, "输入的手机号已经注册过，请直接登录。");
      }

      long time = System.currentTimeMillis();
      // Save user.
      Entity user = new Entity("user");
      user.set("phone", phone);
      user.set("email", "").set("username", ""); // 邮箱、用户名默认为空。
      user.set("password", EncryptUtils.Md5(password, 10));
      user.set("locale", "cn").set("roles", "user").set("bindStatus", 1).set("status", 1).set("createTime", time);
      entityDao.save(user);
      return ResponseBuilder.OK;

    } catch (Throwable t) {
      LOG.error("Error occurs when registering user.", t);
      return ResponseBuilder.error(500, "注册用户失败，请稍后再试!");
    }
  }

  @POST
  @Path("update_phone")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> updatePhone(@FormParam("deviceId") String deviceId, @FormParam("phone") String phone, @FormParam("security_code") String securityCode) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    // Data validation.
    if (StringUtils.isEmpty(phone)) {
      return ResponseBuilder.error(10101, "请输入手机号。");
    }
    if (StringUtils.isNotEmpty(phone) && StringUtils.length(phone) != 11) {
      return ResponseBuilder.error(10102, "请输入有效的手机号。");
    }
    String sessionKey = phone + "_register";
    @SuppressWarnings("unchecked")
    Pair<Long, String> pair = (Pair<Long, String>) getSessionAttribute(sessionKey);
    if (!StringUtils.equals(securityCode, pair.right)) {
      return ResponseBuilder.error(10105, "验证码错误。");
    }

    try {
      if (entityDao.exists("user", "phone", phone)) {
        return ResponseBuilder.error(10106, "你输入的手机号已经绑定过其他帐号。");
      }

      long time = System.currentTimeMillis();
      // 更新绑定的设备ID和绑定状态。
      Map<String, Object> updateValues = new HashMap<String, Object>();
      updateValues.put("phone", phone);
      updateValues.put("deviceId", deviceId);
      updateValues.put("bindStatus", 1);
      updateValues.put("updateTime", time);
      entityDao.update("user", "phone", phone, updateValues);
      LOG.info("User " + user.getId() + " updated phone: " + phone);

      // 更新Session。
      user = entityDao.findOne("user", "phone", phone, UserRowMapper.getInstance());
      setSessionAttribute("_user", user);
      return ResponseBuilder.ok(user.getUsername());

    } catch (Throwable t) {
      LOG.error("Error occurs when binding phone.", t);
      return ResponseBuilder.error(500, "修改手机号码失败，请稍后再试!");
    }
  }

  @POST
  @Path("profile")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> updateProfile(@FormParam("name") String name, @FormParam("value") String value) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (!ArrayUtils.contains(PROFILE_KEYS, name)) {
      return ResponseBuilder.error(10201, "不支持的用户资料信息。");
    }

    if (StringUtils.equals("email", name)) {
      if (StringUtils.isEmpty(value)) {
        return ResponseBuilder.error(10107, "邮箱不能设置为空。");
      }

      if (StringUtils.length(value) > 50) {
        return ResponseBuilder.error(10108, "邮箱不能超过50个字符。");
      }

    } else if (StringUtils.equals("username", name)) {
      if (StringUtils.isEmpty(value)) {
        return ResponseBuilder.error(10109, "用户昵称不能设置为空。");
      }

      if (StringUtils.length(value) < 2) {
        return ResponseBuilder.error(10110, "用户昵称不能少于2个字。");
      }

      if (StringUtils.length(value) > 10) {
        return ResponseBuilder.error(10111, "用户昵称不能超过10个字。");
      }

      String regex = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{2,10}$";
      Pattern pattern = Pattern.compile(regex);
      if (!pattern.matcher(value).matches()) {
        return ResponseBuilder.error(10119, "用户昵称只能使用中文、字母、数字和下划线，不能少于2个字。");
      }

      if (entityDao.exists("user", "username", value)) {
        return ResponseBuilder.error(10120, "用户昵称已经被使用。");
      }

    } else if (StringUtils.equals("avatar", name)) {
      if (StringUtils.isEmpty(value)) {
        return ResponseBuilder.error(10111, "用户头像不能设置为空。");
      }

    } else if (StringUtils.equals("gender", name)) {
      if (StringUtils.isEmpty(value)) {
        return ResponseBuilder.error(10112, "性别不能设置为空。");
      }
      if (StringUtils.length(value) > 1) {
        return ResponseBuilder.error(10113, "性别数据不合法，请重新选择设置。");
      }

    } else if (StringUtils.equals("birthday", name)) {
      if (StringUtils.isEmpty(value)) {
        return ResponseBuilder.error(10114, "生日不能设置为空。");
      }
      if (StringUtils.length(value) > 10) {
        return ResponseBuilder.error(10115, "生日数据不合法，请重新选择设置。");
      }

    } else if (StringUtils.equals("city", name)) {
      if (StringUtils.length(value) > 50) {
        return ResponseBuilder.error(10116, "城市数据不合法，请重新选择设置。");
      }

    } else if (StringUtils.equals("profile", name)) {
      if (StringUtils.length(value) > 30) {
        return ResponseBuilder.error(10118, "用户签名不能超过30个字。");
      }

    } else {
      return ResponseBuilder.error(10201, "不支持的用户资料信息。");
    }

    Map<String, Object> updateValues = new HashMap<String, Object>();
    updateValues.put(name, value);
    updateValues.put("updateTime", System.currentTimeMillis());
    entityDao.update("user", "id", user.getId(), updateValues);

    // 更新Session 缓存。
    User newUser = entityDao.get("user", user.getId(), UserRowMapper.getInstance());
    WebUtils.setSessionAttribute(request, "_user", newUser);
    return ResponseBuilder.OK;
  }

  @POST
  @Path("signin")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> signin(@FormParam("phone") String phone, @FormParam("password") String password, @FormParam("to") String to) {
    User user = entityDao.findOne("user", "phone", phone, UserRowMapper.getInstance());
    if (user == null) {
      LOG.warn("User " + phone + " signs in failed: user not found!");
      return ResponseBuilder.error(10300, "手机号或密码错误。");
    }
    if (user.getStatus() == 0) {
      return ResponseBuilder.error(10301, "用户尚未激活。请首先激活您的账号，然后再登录。");
    }

    long userId = user.getId();
    if (StringUtils.equals(EncryptUtils.Md5(password, 8), user.getPassword())) {
      // 更新Session。
      HttpSession session = request.getSession(false);
      if (session != null) {
        session.invalidate();
      }
      WebUtils.setSessionAttribute(request, "_user", user);

      // Store session id。
      String sessionId = WebUtils.getSessionId(request);
      sessionService.storeSession(userId, sessionId);

      if (StringUtils.isEmpty(to)) {
        to = "/";
      }
      return ResponseBuilder.ok(to);
    }

    LOG.warn("User " + phone + " signs in failed!");
    return ResponseBuilder.error(10300, "手机号或密码错误。");
  }

  @GET
  @Path("signout")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> signout() {
    try {
      // Clear sessionId.
      String sessionId = WebUtils.getSessionId(request);
      if (StringUtils.isNotEmpty(sessionId)) {
        sessionService.destorySession(sessionId);
      }

      request.getSession().invalidate();
      return ResponseBuilder.OK;

    } catch (Throwable t) {
      LOG.error("User can not sign out.", t);
      return ResponseBuilder.error(10302, "退出登录失败。");
    }
  }

  @Override
  protected Map<String, Object> filterUser(User user) {
    if (user == null) {
      return null;
    }

    Map<String, Object> result = new HashMap<String, Object>();
    result.put("id", user.getId());
    result.put("username", user.getUsername());
    result.put("gender", user.getGender());
    result.put("avatar", user.getAvatar());
    result.put("profile", user.getProfile());
    result.put("createTime", user.getCreateTime());
    return result;
  }

}
