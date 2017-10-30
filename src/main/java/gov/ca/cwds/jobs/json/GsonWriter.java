package gov.ca.cwds.jobs.json;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.inject.Singleton;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import com.google.gson.Gson;

@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class GsonWriter<T> implements MessageBodyWriter<T> {

  @Override
  public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
      throws IOException {
    final Gson g = new Gson();

    if (!httpHeaders.containsKey("Content-Type")) {
      httpHeaders.add("Content-Type", "charset=UTF-8");
    } else {
      httpHeaders.get("Content-Type").add("charset=UTF-8");
    }

    entityStream.write(g.toJson(t).getBytes(Charset.defaultCharset()));
  }

  @Override
  public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType) {
    return -1;
  }

  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType) {
    return true;
  }

}
