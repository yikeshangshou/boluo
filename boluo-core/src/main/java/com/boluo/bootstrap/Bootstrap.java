package com.boluo.bootstrap;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author mixueqiang
 * @since Oct 27, 2014
 */
public class Bootstrap {

  @SuppressWarnings("resource")
  public static void main(String[] args) throws Throwable {

    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        new ClassPathXmlApplicationContext(new String[] { "ApplicationContext.xml" });
      }
    });

    thread.setDaemon(false);
    thread.start();

    while (true) {
      Thread.sleep(10000);
    }
  }

}
