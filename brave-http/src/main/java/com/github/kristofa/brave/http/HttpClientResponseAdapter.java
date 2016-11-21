package com.github.kristofa.brave.http;

import com.github.kristofa.brave.ClientResponseAdapter;

public class HttpClientResponseAdapter extends HttpResponseAdapter
    implements ClientResponseAdapter {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends HttpResponseAdapter.Builder<Builder> {

        @Override public HttpClientResponseAdapter build() {
            return new HttpClientResponseAdapter(this);
        }

        Builder() { // intentionally hidden
        }
    }

    /**
     * @deprecated please use {@link #builder()}
     */
    @Deprecated
    public HttpClientResponseAdapter(HttpResponse response) {
        this(builder().response(response));
    }

    HttpClientResponseAdapter(Builder builder) { // intentionally hidden
        super(builder);
    }
}
