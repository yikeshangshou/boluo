package com.boluo.util;

import java.io.StringWriter;
import java.util.Properties;

import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * @author mixueqiang
 * @since May 6, 2014
 */
public class EmailTemplates {

  static {
    // Initialize the velocity context.
    Properties props = new Properties();
    props.put("resource.loader", "class");
    props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    Velocity.init(props);
  }

  public static HtmlEmail getUserActivateEmail(String username, String email, String authCode) throws Exception {
    HtmlEmail htmlEmail = new HtmlEmail();
    htmlEmail.addTo(email, username, "UTF-8");
    htmlEmail.setSubject("Transkip.com 用户注册确认邮件");

    VelocityContext context = new VelocityContext();
    context.put("username", username);
    context.put("email", email);
    context.put("authCode", authCode);
    StringWriter writer = new StringWriter();
    Velocity.getTemplate("email_templates/user_activate.vm", "UTF-8").merge(context, writer);
    htmlEmail.setHtmlMsg(writer.toString());

    return htmlEmail;
  }

  public static HtmlEmail getWorkerActivateEmail(String username, String email, String authCode) throws Exception {
    HtmlEmail htmlEmail = new HtmlEmail();
    htmlEmail.addTo(email, username, "UTF-8");
    htmlEmail.setSubject("Transkip.com 译员注册确认邮件");

    VelocityContext context = new VelocityContext();
    context.put("username", username);
    context.put("email", email);
    context.put("authCode", authCode);
    StringWriter writer = new StringWriter();
    Velocity.getTemplate("email_templates/worker_activate.vm", "UTF-8").merge(context, writer);
    htmlEmail.setHtmlMsg(writer.toString());

    return htmlEmail;
  }

  public static HtmlEmail getCompanyActivateEmail(String username, String email, String authCode) throws Exception {
    HtmlEmail htmlEmail = new HtmlEmail();
    htmlEmail.addTo(email, username, "UTF-8");
    htmlEmail.setSubject("Transkip.com 公司注册确认邮件");

    VelocityContext context = new VelocityContext();
    context.put("username", username);
    context.put("email", email);
    context.put("authCode", authCode);
    StringWriter writer = new StringWriter();
    Velocity.getTemplate("email_templates/company_activate.vm", "UTF-8").merge(context, writer);
    htmlEmail.setHtmlMsg(writer.toString());

    return htmlEmail;
  }

  public static HtmlEmail getPasswordResetEmail(String email, String authCode) throws Exception {
    HtmlEmail htmlEmail = new HtmlEmail();
    htmlEmail.addTo(email, email, "UTF-8");
    htmlEmail.setSubject("Transkip.com 用户密码重置邮件");

    VelocityContext context = new VelocityContext();
    context.put("email", email);
    context.put("authCode", authCode);
    StringWriter writer = new StringWriter();
    Velocity.getTemplate("email_templates/password_reset.vm", "UTF-8").merge(context, writer);
    htmlEmail.setHtmlMsg(writer.toString());

    return htmlEmail;
  }

}
