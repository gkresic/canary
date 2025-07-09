package com.steatoda.canary.server.rest;

import com.steatoda.canary.server.CanaryStatus;
import com.steatoda.canary.server.error.CanaryServerErrorException;
import io.helidon.common.media.type.MediaType;
import io.helidon.common.media.type.MediaTypes;
import io.helidon.http.Status;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import jakarta.inject.Inject;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

import com.steatoda.canary.server.payload.rest.PayloadRouter;
import com.steatoda.canary.server.rest.handler.LogHandler;
import com.steatoda.canary.server.rest.handler.RouteNameHandler;
import com.steatoda.canary.server.rest.handler.TraceHandler;

import javax.inject.Provider;
import java.io.IOException;
import java.io.OutputStream;

public class RootRouter implements HttpService {

	@Inject
	public RootRouter(
		TraceHandler traceHandler,
		LogHandler logHandler,
		RouteNameHandler.Factory routeNameHandlerFactory,
		PayloadRouter payloadRouter,
		Provider<CanaryStatus> statusProvider,
		PrometheusMeterRegistry prometheusMeterRegistry
	) {
		this.traceHandler = traceHandler;
		this.logHandler = logHandler;
		this.routeNameHandlerFactory = routeNameHandlerFactory;
        this.payloadRouter = payloadRouter;
		this.statusProvider = statusProvider;
		this.prometheusMeterRegistry = prometheusMeterRegistry;
	}

	@Override
	public void routing(HttpRules rules) {

		rules.any(
			traceHandler,
			logHandler
		);

		rules.get("/error",
			routeNameHandlerFactory.create("error"),
			this::bug
		);

		rules.get("/status",
			routeNameHandlerFactory.create("status"),
			this::status
		);

		rules.get("/metrics",
			routeNameHandlerFactory.create("metrics"),
			this::metrics
		);

		rules.register("/payload", payloadRouter);

	}

	private void bug(ServerRequest request, ServerResponse response) {
		throw new CanaryServerErrorException("Simulated error");
	}

	private void status(ServerRequest request, ServerResponse response) {
		response.send(statusProvider.get());
	}

	private void metrics(ServerRequest request, ServerResponse response) throws IOException {
		response.status(Status.OK_200);
		MediaType mediaType = MediaTypes.TEXT_PLAIN;
		response.headers().contentType(mediaType);
		try (OutputStream ostream = response.outputStream()) {
			prometheusMeterRegistry.scrape(ostream, mediaType.text());
		}
	}

	private final TraceHandler traceHandler;
	private final LogHandler logHandler;
	private final RouteNameHandler.Factory routeNameHandlerFactory;
	private final PayloadRouter payloadRouter;
	private final Provider<CanaryStatus> statusProvider;
	private final PrometheusMeterRegistry prometheusMeterRegistry;

}
