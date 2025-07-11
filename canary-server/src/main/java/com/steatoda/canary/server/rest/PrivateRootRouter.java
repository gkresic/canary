package com.steatoda.canary.server.rest;

import com.steatoda.canary.server.rest.handler.LogHandler;
import com.steatoda.canary.server.rest.handler.RouteNameHandler;
import com.steatoda.canary.server.rest.handler.TraceHandler;
import io.helidon.common.media.type.MediaType;
import io.helidon.common.media.type.MediaTypes;
import io.helidon.http.Status;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import jakarta.inject.Inject;

import java.io.IOException;
import java.io.OutputStream;

public class PrivateRootRouter extends RootRouter {

	@Inject
	public PrivateRootRouter(
		TraceHandler traceHandler,
		LogHandler logHandler,
		RouteNameHandler.Factory routeNameHandlerFactory,
		PrometheusMeterRegistry prometheusMeterRegistry
	) {
		super (traceHandler, logHandler);
		this.routeNameHandlerFactory = routeNameHandlerFactory;
		this.prometheusMeterRegistry = prometheusMeterRegistry;
	}

	@Override
	public void routing(HttpRules rules) {

		super.routing(rules);

		rules.get("/metrics",
			routeNameHandlerFactory.create("metrics"),
			this::metrics
		);

	}

	private void metrics(ServerRequest request, ServerResponse response) throws IOException {
		response.status(Status.OK_200);
		MediaType mediaType = MediaTypes.TEXT_PLAIN;
		response.headers().contentType(mediaType);
		try (OutputStream ostream = response.outputStream()) {
			prometheusMeterRegistry.scrape(ostream, mediaType.text());
		}
	}

	private final RouteNameHandler.Factory routeNameHandlerFactory;
	private final PrometheusMeterRegistry prometheusMeterRegistry;

}
