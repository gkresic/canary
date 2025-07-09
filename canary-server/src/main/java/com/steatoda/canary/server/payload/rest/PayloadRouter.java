package com.steatoda.canary.server.payload.rest;

import com.steatoda.canary.server.rest.RequestParser;
import jakarta.inject.Inject;

import com.steatoda.canary.server.payload.Payload;
import com.steatoda.canary.server.rest.handler.RouteNameHandler;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

public class PayloadRouter implements HttpService {

	@Inject
	public PayloadRouter(
		RouteNameHandler.Factory routeNameHandlerFactory,
		PayloadInstanceRouter payloadInstanceRouter,
		RequestParser.Factory requestParserFactory
	) {
		this.routeNameHandlerFactory = routeNameHandlerFactory;
		this.payloadInstanceRouter = payloadInstanceRouter;
		this.requestParserFactory = requestParserFactory;
	}

	@Override
	public void routing(HttpRules rules) {

		rules.any(
			routeNameHandlerFactory.create("payload")
		);

		rules.post("/",
			routeNameHandlerFactory.create("create"),
			this::create
		);

		rules.register("/{" + PayloadInstanceRouter.PATH_PARAM_NAME_INSTANCE + "}", payloadInstanceRouter);

	}

	private void create(ServerRequest request, ServerResponse response) {

		RequestParser requestParser = requestParserFactory.create(request);

		Payload payload = requestParser.body(Payload.class);

		response.send(payload);

	}

	private final RouteNameHandler.Factory routeNameHandlerFactory;
	private final PayloadInstanceRouter payloadInstanceRouter;
	private final RequestParser.Factory requestParserFactory;

}
