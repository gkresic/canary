package com.steatoda.canary.server.rest;

import com.steatoda.canary.server.rest.handler.CanaryErrorHandler;
import jakarta.inject.Inject;

import io.helidon.config.Config;
import io.helidon.http.media.MediaContext;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Wraps Helidon's {@link WebServer}, manages its lifecycle and makes sure it is closed on app shutdown.</p>
 */
public class HelidonServer implements RestServer {

	@Inject
	public HelidonServer(
		RootRouter rootRouter,
		Config config,
		MediaContext mediaContext,
		CanaryErrorHandler canaryErrorHandler
	) {

		webServer = WebServer.builder()
			.config(config.get("server"))
			.mediaContext(mediaContext)
			.routing(HttpRouting.builder()
				.register(rootRouter)
				.error(Throwable.class, canaryErrorHandler)
			)
			//.directHandlers(DirectHandlers.builder().addHandler(EventType.BAD_REQUEST, new MyDirectHandler()).build())	// TODO "system" error handlers
			.build();

		LOG.info("Helidon initialized");

	}

	@Override
	public void start() {

		webServer.start();

		LOG.info("Helidon started, listening on port {}", webServer.port());

	}

	@Override
	public void stop() {

		webServer.stop();

		LOG.info("Helidon stopped");

	}

	private static final Logger LOG = LoggerFactory.getLogger(HelidonServer.class);

	private final WebServer webServer;

}
