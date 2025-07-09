package com.steatoda.canary.server.rest.param;

public class IntegerDeserializer extends Deserializer<Integer> {

	public IntegerDeserializer(ExceptionFactory exceptionFactory) {
		super(exceptionFactory);
	}

	@Override
	protected Integer deserialize(String value) {
		return Integer.valueOf(value);
	}

}
