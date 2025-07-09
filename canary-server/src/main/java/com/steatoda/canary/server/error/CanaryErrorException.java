package com.steatoda.canary.server.error;

public class CanaryErrorException extends RuntimeException {
	
	public CanaryErrorException(CanaryError error) {
		super(error.toString());
		this.error = error;
	}

	public CanaryError error() { return error; }

	private final CanaryError error;
	
}
