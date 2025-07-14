package com.steatoda.canary.server.rest.handler;

import java.util.Optional;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

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
	public LogHandler(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
	}

	@Override
	public void handle(ServerRequest request, ServerResponse response) {

		request.context().register(LogHandler.class, Timer.start(meterRegistry));

		response.whenSent(() -> {

			Timer.Sample timerSample = request.context().get(LogHandler.class, Timer.Sample.class).orElseThrow();

			String routeName = extractRouteName(request);

			long durationNano = timerSample.stop(
				Timer.builder("canary.rest.request")
					.description("API requests")
					.tag("service", routeName)
					.tag("status", String.valueOf(response.status().code()))
					.publishPercentiles(0.5, 0.9, 0.95, 0.99)
					.register(meterRegistry)
			);

			LOG.info("Served {} for {} in {}ms", routeName, request.remotePeer().address(), Math.round(durationNano / 1_000_000.0));

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

	private final MeterRegistry meterRegistry;

}
