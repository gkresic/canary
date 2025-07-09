package com.steatoda.canary.server.error;

public class CanaryTimeoutException extends CanaryErrorException {

	public CanaryTimeoutException() {
		super(new CanaryError(CanaryError.Code.TIMEOUT, "Timeout"));
	}

}
