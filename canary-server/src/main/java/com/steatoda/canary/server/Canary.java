package com.steatoda.canary.server;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import com.steatoda.canary.server.rest.RestServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Canary {

	@Inject
	@SuppressWarnings("unused")	// extra parameters are injected only to be initialized as soon as possible ("eager singletons")
	public Canary(
			RestServer restServer, MeterRegistry meterRegistry
	) {
		this.restServer = restServer;
		this.meterRegistry = meterRegistry;
	}

	public void start() {
		
		LOG.debug("Canary {} starting...", CanaryProperties.get().getVersion());

		restServer.start();

		LOG.info("Canary {} is up and runnin'", CanaryProperties.get().getVersion());

	}
	
	public void stop() {
		
		LOG.debug("Canary {} stopping...", CanaryProperties.get().getVersion());

		restServer.stop();

		LOG.info("Canary {} stopped", CanaryProperties.get().getVersion());

	}
	
	public void destroy() {

		LOG.debug("Canary {} is initializing shutdown...", CanaryProperties.get().getVersion());

		meterRegistry.close();
		
		LOG.info("Canary {} successfully shut down - bye!", CanaryProperties.get().getVersion());
		
	}

	private static final Logger LOG = LoggerFactory.getLogger(Canary.class);

	private final RestServer restServer;
	private final MeterRegistry meterRegistry;

}
