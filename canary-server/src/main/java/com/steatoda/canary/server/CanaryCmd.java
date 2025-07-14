package com.steatoda.canary.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CanaryCmd {

	public static void main(String[] args) {

		LOG.info(
			"""
			
			
				    \\      ____                            \s
				    (o>   / ___|__ _ _ __   __ _ _ __ _   _\s
				\\\\_//)   | |   / _` | '_ \\ / _` | '__| | | |
				 \\_/_)   | |__| (_| | | | | (_| | |  | |_| |
				  _|_     \\____\\__,_|_| |_|\\__,_|_|   \\__, |
				                                      |___/\s
			
				:: Canary :: {}
			""",
			CanaryProperties.get().getVersion()
		);

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
