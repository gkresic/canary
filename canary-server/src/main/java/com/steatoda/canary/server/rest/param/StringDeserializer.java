package com.steatoda.canary.server.rest.param;

public class StringDeserializer extends Deserializer<String> {

	public StringDeserializer(ExceptionFactory exceptionFactory) {
		super(exceptionFactory);
	}

	@Override
	protected String deserialize(String value) {
		return value;
	}

}
