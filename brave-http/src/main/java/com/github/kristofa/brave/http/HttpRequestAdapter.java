package com.github.kristofa.brave.http;

import com.github.kristofa.brave.KeyValueAnnotation;
import com.github.kristofa.brave.TagExtractor;
import com.github.kristofa.brave.internal.TagExtractorBuilder;
import java.util.Collection;
import zipkin.TraceKeys;

class HttpRequestAdapter<R extends HttpRequest> {

  final R request;
  private final SpanNameProvider spanNameProvider;
  private final TagExtractor<R> tagExtractor;

  static abstract class Builder<R extends HttpRequest, B extends Builder<R, B>>
      implements TagExtractor.Config<B> {
    private final TagExtractorBuilder tagExtractorBuilder = TagExtractorBuilder.create()
        .addKey(TraceKeys.HTTP_URL)
        .addValueParserFactory(new HttpRequestValueParserFactory());
    private R request;
    private SpanNameProvider spanNameProvider = new DefaultSpanNameProvider();

    public B request(R request) {
      this.request = request;
      return (B) this;
    }

    public B spanNameProvider(SpanNameProvider spanNameProvider) {
      this.spanNameProvider = spanNameProvider;
      return (B) this;
    }

    @Override public B addKey(String key) {
      tagExtractorBuilder.addKey(key);
      return (B) this;
    }

    @Override public B addValueParserFactory(TagExtractor.ValueParserFactory factory) {
      tagExtractorBuilder.addValueParserFactory(factory);
      return (B) this;
    }

    abstract HttpRequestAdapter<R> build();

    Builder() { // intentionally hidden
    }
  }

  HttpRequestAdapter(Builder<R, ?> builder) { // intentionally hidden
    this.request = builder.request;
    this.spanNameProvider = builder.spanNameProvider;
    this.tagExtractor = builder.tagExtractorBuilder
        .build((Class) request.getClass());
  }

  public String getSpanName() {
    return spanNameProvider.spanName(request);
  }

  public Collection<KeyValueAnnotation> requestAnnotations() {
    return tagExtractor.extractTags(request);
  }
}
