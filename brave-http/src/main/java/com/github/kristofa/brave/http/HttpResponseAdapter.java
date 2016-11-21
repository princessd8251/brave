package com.github.kristofa.brave.http;

import com.github.kristofa.brave.KeyValueAnnotation;
import com.github.kristofa.brave.TagExtractor;
import com.github.kristofa.brave.TagExtractor.ValueParserFactory;
import com.github.kristofa.brave.internal.TagExtractorBuilder;
import java.util.Collection;
import zipkin.TraceKeys;

class HttpResponseAdapter {
  private final HttpResponse response;
  private final TagExtractor<HttpResponse> tagExtractor;

  static abstract class Builder<B extends Builder<B>> implements TagExtractor.Config<B> {
    private final TagExtractorBuilder tagExtractorBuilder = TagExtractorBuilder.create()
        .addKey(TraceKeys.HTTP_STATUS_CODE)
        .addValueParserFactory(new HttpResponseValueParserFactory());
    private HttpResponse response;

    public B response(HttpResponse response) {
      this.response = response;
      return (B) this;
    }

    @Override public B addKey(String key) {
      tagExtractorBuilder.addKey(key);
      return (B) this;
    }

    @Override public B addValueParserFactory(ValueParserFactory factory) {
      tagExtractorBuilder.addValueParserFactory(factory);
      return (B) this;
    }

    public abstract HttpResponseAdapter build();

    Builder() { // intentionally hidden
    }
  }

  HttpResponseAdapter(Builder builder) { // intentionally hidden
    this.response = builder.response;
    this.tagExtractor = builder.tagExtractorBuilder.<HttpResponse>build(response.getClass());
  }

  public Collection<KeyValueAnnotation> responseAnnotations() {
    return tagExtractor.extractTags(response);
  }
}
