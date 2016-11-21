package com.github.kristofa.brave.http;

import com.github.kristofa.brave.ServerRequestAdapter;
import com.github.kristofa.brave.SpanId;
import com.github.kristofa.brave.TraceData;

import static com.github.kristofa.brave.IdConversion.convertToLong;

public class HttpServerRequestAdapter extends HttpRequestAdapter<HttpServerRequest>
    implements ServerRequestAdapter {

    private static final TraceData EMPTY_UNSAMPLED_TRACE = TraceData.builder().sample(false).build();
    private static final TraceData EMPTY_MAYBE_TRACE = TraceData.builder().build();

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder
        extends HttpRequestAdapter.Builder<HttpServerRequest, Builder> {

        @Override
        public HttpServerRequestAdapter build() {
            return new HttpServerRequestAdapter(this);
        }

        Builder() { // intentionally hidden
        }
    }

    /**
     * @deprecated please use {@link #builder()}
     */
    @Deprecated
    public HttpServerRequestAdapter(HttpServerRequest request, SpanNameProvider spanNameProvider) {
        this(builder().request(request).spanNameProvider(spanNameProvider));
    }

    HttpServerRequestAdapter(Builder builder) { // intentionally hidden
        super(builder);
    }

    @Override
    public TraceData getTraceData() {
        final String sampled = request.getHttpHeaderValue(BraveHttpHeaders.Sampled.getName());
        if (sampled != null) {
            if (sampled.equals("0") || sampled.equalsIgnoreCase("false")) {
                return EMPTY_UNSAMPLED_TRACE;
            } else {
                final String parentSpanId = request.getHttpHeaderValue(BraveHttpHeaders.ParentSpanId.getName());
                final String traceId = request.getHttpHeaderValue(BraveHttpHeaders.TraceId.getName());
                final String spanId = request.getHttpHeaderValue(BraveHttpHeaders.SpanId.getName());

                if (traceId != null && spanId != null) {
                    SpanId span = getSpanId(traceId, spanId, parentSpanId);
                    return TraceData.builder().sample(true).spanId(span).build();
                }
            }
        }
        return EMPTY_MAYBE_TRACE;
    }

    private SpanId getSpanId(String traceId, String spanId, String parentSpanId) {
        return SpanId.builder()
            .traceIdHigh(traceId.length() == 32 ? convertToLong(traceId, 0) : 0)
            .traceId(convertToLong(traceId))
            .spanId(convertToLong(spanId))
            .parentId(parentSpanId == null ? null : convertToLong(parentSpanId)).build();
   }
}
