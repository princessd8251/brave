package com.github.kristofa.brave.http;

import com.github.kristofa.brave.TagExtractor;
import java.lang.reflect.Type;
import zipkin.TraceKeys;

final class HttpRequestValueParserFactory implements TagExtractor.ValueParserFactory {
  @Override public TagExtractor.ValueParser<?> create(Type type, String key) {
    if (!(type instanceof Class)) return null;
    if (!HttpRequest.class.isAssignableFrom((Class) type)) return null;

    if (key.equals(TraceKeys.HTTP_HOST)) { // Can't switch on string since minimum JRE 6
      return new TagExtractor.ValueParser<HttpRequest>() {
        @Override public String parse(HttpRequest input) {
          return input.getUri().getHost();
        }
      };
    } else if (key.equals(TraceKeys.HTTP_METHOD)) {
      return new TagExtractor.ValueParser<HttpRequest>() {
        @Override public String parse(HttpRequest input) {
          return input.getHttpMethod();
        }
      };
    } else if (key.equals(TraceKeys.HTTP_PATH)) {
      return new TagExtractor.ValueParser<HttpRequest>() {
        @Override public String parse(HttpRequest input) {
          return input.getUri().getPath();
        }
      };
    } else if (key.equals(TraceKeys.HTTP_URL)) {
      return new TagExtractor.ValueParser<HttpRequest>() {
        @Override public String parse(HttpRequest input) {
          return input.getUri().toASCIIString();
        }
      };
    } else if (key.equals(TraceKeys.HTTP_REQUEST_SIZE) && type instanceof HttpServerRequest) {
      return new TagExtractor.ValueParser<HttpServerRequest>() {
        @Override public String parse(HttpServerRequest input) {
          return input.getHttpHeaderValue("Content-Length");
        }
      };
    } else {
      return null;
    }
  }
}
