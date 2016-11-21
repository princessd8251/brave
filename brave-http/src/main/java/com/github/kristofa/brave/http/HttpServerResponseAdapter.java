package com.github.kristofa.brave.http;

import com.github.kristofa.brave.ServerResponseAdapter;

public class HttpServerResponseAdapter extends HttpResponseAdapter
    implements ServerResponseAdapter {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends HttpResponseAdapter.Builder<Builder> {

        @Override public HttpServerResponseAdapter build() {
            return new HttpServerResponseAdapter(this);
        }

        Builder() { // intentionally hidden
        }
    }

    /**
     * @deprecated please use {@link #builder()}
     */
    @Deprecated
    public HttpServerResponseAdapter(HttpResponse response) {
        this(builder().response(response));
    }

    HttpServerResponseAdapter(Builder builder) { // intentionally hidden
        super(builder);
    }
}
