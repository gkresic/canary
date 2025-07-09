package com.steatoda.canary.server.rest;

import com.steatoda.canary.server.CanaryStatus;
import com.steatoda.canary.server.error.CanaryServerErrorException;
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

public class RootRouter implements HttpService {

	@Inject
	public RootRouter(TraceHandler traceHandler, LogHandler logHandler, RouteNameHandler.Factory routeNameHandlerFactory, PayloadRouter payloadRouter, Provider<CanaryStatus> statusProvider) {
		this.traceHandler = traceHandler;
		this.logHandler = logHandler;
		this.routeNameHandlerFactory = routeNameHandlerFactory;
        this.payloadRouter = payloadRouter;
		this.statusProvider = statusProvider;
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

		rules.register("/payload", payloadRouter);

	}

	private void bug(ServerRequest request, ServerResponse response) {
		throw new CanaryServerErrorException("Simulated error");
	}

	private void status(ServerRequest request, ServerResponse response) {
		response.send(statusProvider.get());
	}

	private final TraceHandler traceHandler;
	private final LogHandler logHandler;
	private final RouteNameHandler.Factory routeNameHandlerFactory;
	private final PayloadRouter payloadRouter;
	private final Provider<CanaryStatus> statusProvider;

}
