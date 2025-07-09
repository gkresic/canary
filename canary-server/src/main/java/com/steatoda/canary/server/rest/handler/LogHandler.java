package com.steatoda.canary.server.rest.handler;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

/**
 * Logs REST request.
 */
@Singleton
public class LogHandler implements Handler {

	@Inject
	public LogHandler() {}

	@Override
	public void handle(ServerRequest request, ServerResponse response) {

		request.context().register(LogHandler.class, StopWatch.createStarted());

		response.whenSent(() -> {

			StopWatch stopwatch = request.context().get(LogHandler.class, StopWatch.class).orElseThrow();

			// TODO instead of logging, report to Prometheus
			LOG.info("Served {} @ {} {} {} in {}ms",
				request.prologue().method().text(),
				request.path().absolute().path(),
				extractRouteName(request),
				request.remotePeer().address(),
				stopwatch.getTime(TimeUnit.MILLISECONDS)
			);

		});

		response.next();

	}

	private String extractRouteName(ServerRequest request) {

		Optional<String> maybeRouteName = RouteNameHandler.get(request);

		if (maybeRouteName.isPresent())
			return maybeRouteName.get();

		LOG.warn("Route {} @ {} doesn't have any name defined", request.prologue().method().text(), request.path().path());

		return "UNKNOWN";

	}

	private static final Logger LOG = LoggerFactory.getLogger(LogHandler.class);

}
