package com.steatoda.canary.server.rest.param;

public class BooleanDeserializer extends Deserializer<Boolean> {

	public BooleanDeserializer(ExceptionFactory exceptionFactory) {
		super(exceptionFactory);
	}

	@Override
	protected Boolean deserialize(String value) {
		return Boolean.valueOf(value);
	}

}
