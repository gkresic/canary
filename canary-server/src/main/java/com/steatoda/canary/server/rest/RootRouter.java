package com.steatoda.canary.server.rest;

import com.steatoda.canary.server.rest.handler.LogHandler;
import com.steatoda.canary.server.rest.handler.TraceHandler;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;

public abstract class RootRouter implements HttpService {

	private final TraceHandler traceHandler;
	private final LogHandler logHandler;

	protected RootRouter(TraceHandler traceHandler, LogHandler logHandler) {
		this.traceHandler = traceHandler;
		this.logHandler = logHandler;
	}

	@Override
	public void routing(HttpRules rules) {

		rules.any(
			traceHandler,
			logHandler
		);

	}

}
