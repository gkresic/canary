package com.steatoda.canary.server.payload.rest;

import com.steatoda.canary.server.payload.Payload;
import com.steatoda.canary.server.rest.RequestParser;
import com.steatoda.canary.server.rest.handler.RouteNameHandler;
import com.steatoda.canary.server.rest.param.Deserializers;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PayloadInstanceRouter implements HttpService {

	public static final String PATH_PARAM_NAME_INSTANCE = "payload";

	@Inject
	public PayloadInstanceRouter(RouteNameHandler.Factory routeNameHandlerFactory, RequestParser.Factory requestParserFactory, Deserializers deserializers) {
		this.routeNameHandlerFactory = routeNameHandlerFactory;
		this.requestParserFactory = requestParserFactory;
		this.deserializers = deserializers;
	}

	@Override
	public void routing(HttpRules rules) {

		rules.any(
			routeNameHandlerFactory.create("{payload}")
		);

		rules.get("/",
			routeNameHandlerFactory.create("get"),
			this::get
		);

	}

	private void get(ServerRequest request, ServerResponse response) {

		RequestParser requestParser = requestParserFactory.create(request);

		int payloadCode = requestParser.pathParam(PATH_PARAM_NAME_INSTANCE, deserializers.integer());

		LOG.info("Payload instance: {}", payloadCode);

		Payload payload = new Payload();
		payload.text = "foo";
		payload.number = payloadCode;

		response.send(payload);

	}

	private static final Logger LOG = LoggerFactory.getLogger(PayloadInstanceRouter.class);

	private final RouteNameHandler.Factory routeNameHandlerFactory;
	private final RequestParser.Factory requestParserFactory;
	private final Deserializers deserializers;

}
