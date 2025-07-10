package com.steatoda.canary.server;

import io.helidon.webserver.WebServer;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Canary {

	@Inject
	@SuppressWarnings("unused")	// extra parameters are injected only to be initialized as soon as possible ("eager singletons")
	public Canary(
		WebServer helidonWebServer,
		MeterRegistry meterRegistry
	) {
		this.helidonWebServer = helidonWebServer;
		this.meterRegistry = meterRegistry;
	}

	public void start() {
		
		LOG.debug("Canary {} starting...", CanaryProperties.get().getVersion());

		helidonWebServer.start();

		LOG.info("Canary {} is up and runnin'", CanaryProperties.get().getVersion());

	}
	
	public void stop() {
		
		LOG.debug("Canary {} stopping...", CanaryProperties.get().getVersion());

		helidonWebServer.stop();

		LOG.info("Canary {} stopped", CanaryProperties.get().getVersion());

	}
	
	public void destroy() {

		LOG.debug("Canary {} is initializing shutdown...", CanaryProperties.get().getVersion());

		meterRegistry.close();
		
		LOG.info("Canary {} successfully shut down - bye!", CanaryProperties.get().getVersion());
		
	}

	private static final Logger LOG = LoggerFactory.getLogger(Canary.class);

	private final WebServer helidonWebServer;
	private final MeterRegistry meterRegistry;

}
