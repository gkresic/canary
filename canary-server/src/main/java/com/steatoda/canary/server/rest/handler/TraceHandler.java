package com.steatoda.canary.server.rest.handler;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.apache.commons.text.TextStringBuilder;

import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

/**
 * Initializes tracing context.
 */
@Singleton
public class TraceHandler implements Handler {

	@Inject
	public TraceHandler() {}

	@Override
	public void handle(ServerRequest request, ServerResponse response) {

		// TODO initialize trace context

		response.whenSent(() -> {
			// TODO clear trace context
		});

		response.next();

	}

}
