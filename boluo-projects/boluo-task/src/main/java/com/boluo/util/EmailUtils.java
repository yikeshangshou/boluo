package com.boluo.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.Email;

/**
 * @author mixueqiang
 * @since May 6, 2014
 */
public class EmailUtils {
  private static final Log LOG = LogFactory.getLog(EmailUtils.class);
  // Setup Email account information.
  private static String HOST_NAME = "smtp.exmail.qq.com";
  private static int PORT = 465;
  private static String USERNAME = "";
  private static String PASSWORD = "";
  private static String CHARSET = "UTF-8";
  private static Map<String, String> users = new HashMap<String, String>();

  static {
    users.put(USERNAME, PASSWORD);
    // Setup Email account information.
    users.put("", "");
  }

  /**
   * <p>
   * 
   * <pre>
   * Email email = new SimpleEmail();
   * email.addTo(&quot;to@example.com&quot;);
   * email.setFrom(&quot;from@example.com&quot;, &quot;From Name&quot;);
   * email.setSubject(&quot;Email Test&quot;);
   * email.setMsg(&quot;It is a test!&quot;);
   * EmailUtils.send(email);
   * </pre>
   * 
   * </p>
   */
  public static final boolean send(Email email) {
    return send(USERNAME, email);
  }

  /**
   * <p>
   * 
   * <pre>
   * Email email = new SimpleEmail();
   * email.addTo(&quot;to@example.com&quot;);
   * email.setFrom(&quot;from@example.com&quot;, &quot;From Name&quot;);
   * email.setSubject(&quot;Email Test&quot;);
   * email.setMsg(&quot;It is a test!&quot;);
   * EmailUtils.send(&quot;no-reply@transkip.com&quot;, email);
   * </pre>
   * 
   * </p>
   */
  public static final boolean send(String specifiedUsername, Email email) {
    email.setHostName(HOST_NAME);
    email.setSmtpPort(PORT);
    String username = USERNAME;
    if (users.containsKey(specifiedUsername)) {
      username = specifiedUsername;
    }
    String password = users.get(username);
    email.setAuthentication(username, password);
    email.setSSL(true);
    email.setCharset(CHARSET);

    try {
      email.setFrom(username, "Transkip翻译资源网");
      email.send();

      LOG.info("Email sent to: " + email.getToAddresses() + ", " + email.getSubject());
      return true;

    } catch (Throwable t) {
      LOG.error("Email send error: " + email.getSubject() + " - " + email.getToAddresses(), t);
      return false;
    }
  }

}
