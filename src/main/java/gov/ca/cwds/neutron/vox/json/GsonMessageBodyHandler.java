package gov.ca.cwds.neutron.vox.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class GsonMessageBodyHandler
    implements MessageBodyWriter<Object>, MessageBodyReader<Object> {

  private Gson gson;

  private Gson getGson() {
    if (gson == null) {
      final GsonBuilder gsonBuilder = new GsonBuilder();
      gson = gsonBuilder.create();
    }
    return gson;
  }

  @Override
  public boolean isReadable(Class<?> type, Type genericType,
      java.lang.annotation.Annotation[] annotations, MediaType mediaType) {
    return true;
  }

  @Override
  public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
      throws IOException {
    try (InputStreamReader reader = new InputStreamReader(entityStream, Charset.defaultCharset())) {
      return getGson().fromJson(reader, type.equals(genericType) ? type : genericType);
    }
  }

  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType) {
    return true;
  }

  @Override
  public long getSize(Object object, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType) {
    return -1;
  }

  @Override
  public void writeTo(Object object, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
      throws IOException {
    try (OutputStreamWriter writer =
        new OutputStreamWriter(entityStream, Charset.defaultCharset())) {
      getGson().toJson(object, type.equals(genericType) ? type : genericType, writer);
    }
  }

}
