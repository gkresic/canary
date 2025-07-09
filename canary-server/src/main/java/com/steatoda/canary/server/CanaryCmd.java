package com.steatoda.canary.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CanaryCmd {

	public static void main(String[] args) {

		LOG.info("**************************************************");
		LOG.info("Welcome to Canary {}...", CanaryProperties.get().getVersion());
		LOG.info("**************************************************");

		LOG.debug("Canary {} initializing...", CanaryProperties.get().getVersion());

		CanaryComponent canaryComponent = DaggerCanaryComponent.create();

		final Canary canary = canaryComponent.canary();

		LOG.info("Canary {} initialized", CanaryProperties.get().getVersion());

		canary.start();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			canary.stop();
			canary.destroy();
		}));

		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			throw new RuntimeException("Main thread interrupted?!", e);
		}

	}

	private static final Logger LOG = LoggerFactory.getLogger(CanaryCmd.class);

}
