package com.github.kristofa.brave.http;

import com.github.kristofa.brave.TagExtractor;
import java.lang.reflect.Type;
import zipkin.TraceKeys;

final class HttpResponseValueParserFactory implements TagExtractor.ValueParserFactory {
  @Override public TagExtractor.ValueParser<?> create(Type type, String key) {
    if (!(type instanceof Class)) return null;
    if (!HttpResponse.class.isAssignableFrom((Class) type)) return null;

    if (key.equals(TraceKeys.HTTP_STATUS_CODE)) { // Can't switch on string since minimum JRE 6
      return new TagExtractor.ValueParser<HttpResponse>() {
        @Override public String parse(HttpResponse input) {
          int httpStatus = input.getHttpStatusCode();
          return httpStatus < 200 || httpStatus > 299 ? String.valueOf(httpStatus) : null;
        }
      };
    } else {
      return null;
    }
  }
}
