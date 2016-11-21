package com.github.kristofa.brave.http;

import com.github.kristofa.brave.ClientRequestAdapter;
import com.github.kristofa.brave.IdConversion;
import com.github.kristofa.brave.SpanId;
import com.github.kristofa.brave.internal.Nullable;
import com.twitter.zipkin.gen.Endpoint;

public class HttpClientRequestAdapter extends HttpRequestAdapter<HttpClientRequest>
    implements ClientRequestAdapter {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder
        extends HttpRequestAdapter.Builder<HttpClientRequest, Builder> {

        @Override
        public HttpClientRequestAdapter build() {
            return new HttpClientRequestAdapter(this);
        }

        Builder() { // intentionally hidden
        }
    }

    /**
     * @deprecated please use {@link #builder()}
     */
    @Deprecated
    public HttpClientRequestAdapter(HttpClientRequest request, SpanNameProvider spanNameProvider) {
        this(builder().request(request).spanNameProvider(spanNameProvider));
    }

    HttpClientRequestAdapter(HttpClientRequestAdapter.Builder builder) { // intentionally hidden
        super(builder);
    }

    @Override
    public void addSpanIdToRequest(@Nullable SpanId spanId) {
        if (spanId == null) {
            request.addHeader(BraveHttpHeaders.Sampled.getName(), "0");
        } else {
            request.addHeader(BraveHttpHeaders.Sampled.getName(), "1");
            request.addHeader(BraveHttpHeaders.TraceId.getName(), spanId.traceIdString());
            request.addHeader(BraveHttpHeaders.SpanId.getName(), IdConversion.convertToString(spanId.spanId));
            if (spanId.nullableParentId() != null) {
                request.addHeader(BraveHttpHeaders.ParentSpanId.getName(), IdConversion.convertToString(spanId.parentId));
            }
        }
    }

    @Override
    public Endpoint serverAddress() {
        return null;
    }
}
