package com.dabllo.util;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.junit.Test;

import com.dabllo.util.EmailUtils;

/**
 * @author mixueqiang
 * @since May 6, 2014
 */
public class EmailUtilsTest {

  @Test
  public void testSendEmail() throws Exception {
    Email email = new SimpleEmail();
    email.addTo("xueqiang.mi@qq.com");
    email.setFrom("no-reply@transkip.com", "翻译资源网");
    email.setSubject("Transkip.com test email");
    email.setMsg("It is a test email!");
    EmailUtils.send(email);
  }

}
