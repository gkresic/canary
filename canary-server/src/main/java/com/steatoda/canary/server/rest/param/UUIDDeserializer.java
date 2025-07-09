package com.steatoda.canary.server.rest.param;

import java.util.UUID;

public class UUIDDeserializer extends Deserializer<UUID> {

	public UUIDDeserializer(ExceptionFactory exceptionFactory) {
		super(exceptionFactory);
	}

	@Override
	protected UUID deserialize(String value) {
		return UUID.fromString(value);
	}

}
