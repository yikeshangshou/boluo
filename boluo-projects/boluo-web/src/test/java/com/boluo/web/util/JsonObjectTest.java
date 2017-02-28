package com.boluo.web.util;

import org.junit.Test;

import com.google.gson.JsonObject;

/**
 * @author mixueqiang
 * @since Jul 30, 2016
 */
public class JsonObjectTest {

  @Test
  public void testToString() {
    JsonObject json = new JsonObject();
    json.addProperty("foo", "bar");
    json.addProperty("a", "b");

    System.out.println(json.toString());
  }

}
