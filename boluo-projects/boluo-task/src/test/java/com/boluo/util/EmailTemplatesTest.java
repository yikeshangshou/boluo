package com.boluo.util;

import org.apache.commons.mail.HtmlEmail;
import org.junit.Assert;
import org.junit.Test;

import com.boluo.util.EmailTemplates;
import com.boluo.util.EmailUtils;

/**
 * @author mixueqiang
 * @since May 6, 2014
 */
public class EmailTemplatesTest {

  @Test
  public void testGetEmail() throws Exception {
    HtmlEmail email = EmailTemplates.getWorkerActivateEmail("宓学强", "xueqiang.mi@qq.com", "123456789");
    Assert.assertNotNull(email);
    email.setFrom("no-reply@transkip.com", "翻译资源网");
    EmailUtils.send(email);
  }

}
