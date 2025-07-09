package com.steatoda.canary.server.rest.param;

import java.time.Instant;

public class InstantDeserializer extends Deserializer<Instant> {

	public InstantDeserializer(ExceptionFactory exceptionFactory) {
		super(exceptionFactory);
	}

	@Override
	protected Instant deserialize(String value) {
		return Instant.ofEpochMilli(Long.parseLong(value));
	}

}
