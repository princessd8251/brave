package com.github.kristofa.brave.jersey;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.kristofa.brave.ClientRequestInterceptor;
import com.github.kristofa.brave.ClientResponseInterceptor;
import com.github.kristofa.brave.http.HttpClientRequestAdapter;
import com.github.kristofa.brave.http.HttpClientResponseAdapter;
import com.github.kristofa.brave.http.SpanNameProvider;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;


/**
 * This filter creates or forwards trace headers and sends cs and cr annotations. Usage:
 *
 * <pre>
 * Client client = Client.create()
 * client.addFilter(new ClientTraceFilter(clientTracer));
 * </pre>
 */
@Singleton
public class JerseyClientTraceFilter extends ClientFilter {

    private final ClientRequestInterceptor clientRequestInterceptor;
    private final ClientResponseInterceptor clientResponseInterceptor;
    private final SpanNameProvider spanNameProvider;

    @Inject
    public JerseyClientTraceFilter(SpanNameProvider spanNameProvider, ClientRequestInterceptor requestInterceptor, ClientResponseInterceptor responseInterceptor) {
        this.spanNameProvider = spanNameProvider;
        this.clientRequestInterceptor = requestInterceptor;
        this.clientResponseInterceptor = responseInterceptor;
    }

    @Override
    public ClientResponse handle(final ClientRequest clientRequest) throws ClientHandlerException {
        // TODO: change this to a factory method (on request) to reduce redundant work
        HttpClientRequestAdapter requestAdapter = HttpClientRequestAdapter.builder()
            .spanNameProvider(spanNameProvider)
            .request(new JerseyHttpRequest(clientRequest))
            .build();
        clientRequestInterceptor.handle(requestAdapter);
        final ClientResponse clientResponse = getNext().handle(clientRequest);
        // TODO: change this to a factory method (on response) to reduce redundant work
        HttpClientResponseAdapter responseAdapter = HttpClientResponseAdapter.builder()
            .response(new JerseyHttpResponse(clientResponse))
            .build();
        clientResponseInterceptor.handle(responseAdapter);
        return clientResponse;
    }
}
