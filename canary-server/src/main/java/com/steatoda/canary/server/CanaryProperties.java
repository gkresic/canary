package com.steatoda.canary.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.util.Properties;

/**
 * <p>Main Canary properties.</p>
 */
public class CanaryProperties extends Properties {

	public static CanaryProperties get() {
		if (instance == null)
			instance = new CanaryProperties();
		return instance;
	}

	private CanaryProperties() {

		super();

		try (InputStream istream = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/steatoda/canary/canary.properties")) {
			load(istream);
			version = getProperty("version");
		} catch (IOException e) {
			throw new RuntimeException("Could not load properties", e);
		}

	}

	public String getVersion() { return version; }

	@Serial
	private static final long serialVersionUID = 1L;

	private static CanaryProperties instance = null;
	
	private final String version;

}
