package com.steatoda.canary.server.rest;

import com.steatoda.canary.server.rest.handler.LogHandler;
import com.steatoda.canary.server.rest.handler.RouteNameHandler;
import com.steatoda.canary.server.rest.handler.TraceHandler;
import io.helidon.common.media.type.MediaType;
import io.helidon.common.media.type.MediaTypes;
import io.helidon.http.Status;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.io.IOException;
import java.io.OutputStream;

public class PrivateRootRouter extends RootRouter {

	@Inject
	public PrivateRootRouter(
		TraceHandler traceHandler,
		LogHandler logHandler,
		RouteNameHandler.Factory routeNameHandlerFactory,
		PrometheusMeterRegistry prometheusMeterRegistry,
		Provider<WebServer> helidonWebServerProvider
	) {
		super (traceHandler, logHandler);
		this.routeNameHandlerFactory = routeNameHandlerFactory;
		this.prometheusMeterRegistry = prometheusMeterRegistry;
		this.helidonWebServerProvider = helidonWebServerProvider;
	}

	@Override
	public void routing(HttpRules rules) {

		super.routing(rules);

		rules.get("/metrics",
			routeNameHandlerFactory.create("metrics"),
			this::metrics
		);

		rules.get("/healthy",
			routeNameHandlerFactory.create("healthy"),
			this::healthy
		);

		rules.get("/stop",
			routeNameHandlerFactory.create("stop"),
			this::stop
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

	private void healthy(ServerRequest request, ServerResponse response) {
		response
			.status(Status.NO_CONTENT_204)	// in case of sickness, return Status.SERVICE_UNAVAILABLE_503
			.send();
	}

	private void stop(ServerRequest request, ServerResponse response) {
		response
			.send("Bye!");
		helidonWebServerProvider.get().stop();
	}

	private final RouteNameHandler.Factory routeNameHandlerFactory;
	private final PrometheusMeterRegistry prometheusMeterRegistry;
	private final Provider<WebServer> helidonWebServerProvider;

}
