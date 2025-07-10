package com.steatoda.canary.server.rest.handler;

import io.micrometer.tracing.ScopedSpan;
import io.micrometer.tracing.Tracer;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializes tracing context.
 */
@Singleton
public class TraceHandler implements Handler {

	@Inject
	public TraceHandler(Tracer tracer) {
		this.tracer = tracer;
	}

	@Override
	@SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr"})
	public void handle(ServerRequest request, ServerResponse response) {

		ScopedSpan span = tracer.startScopedSpan("rest");

		response.whenSent(() -> {

			span.end();

		});

		response.next();

	}

	private static final Logger LOG = LoggerFactory.getLogger(TraceHandler.class);

	private final Tracer tracer;

}
