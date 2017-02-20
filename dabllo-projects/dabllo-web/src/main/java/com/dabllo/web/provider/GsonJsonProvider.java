package com.dabllo.web.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.dabllo.web.BaseResource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author mixueqiang
 * @since May 25, 2016
 */
@Provider
@Consumes(BaseResource.APPLICATION_JSON)
@Produces(BaseResource.APPLICATION_JSON)
public class GsonJsonProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

  private Gson gson;

  @Override
  public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return -1;
  }

  @Override
  public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return true;
  }

  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return true;
  }

  @Override
  public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
      throws IOException, WebApplicationException {
    InputStreamReader streamReader = new InputStreamReader(entityStream, "UTF-8");

    try {
      Type jsonType;
      if (type.equals(genericType)) {
        jsonType = type;
      } else {
        jsonType = genericType;
      }

      return getGson().fromJson(streamReader, jsonType);

    } finally {
      streamReader.close();
    }
  }

  @Override
  public void writeTo(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
      throws IOException, WebApplicationException {
    OutputStreamWriter writer = new OutputStreamWriter(entityStream, "UTF-8");

    try {
      Type jsonType;
      if (type.equals(genericType)) {
        jsonType = type;
      } else {
        jsonType = genericType;
      }

      getGson().toJson(object, jsonType, writer);

    } finally {
      writer.close();
    }
  }

  private Gson getGson() {
    if (gson == null) {
      final GsonBuilder gsonBuilder = new GsonBuilder();
      gson = gsonBuilder.create();
    }

    return gson;
  }
}
