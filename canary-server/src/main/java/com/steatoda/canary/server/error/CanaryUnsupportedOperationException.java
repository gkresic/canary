package com.steatoda.canary.server.error;

public class CanaryUnsupportedOperationException extends CanaryErrorException {

	public CanaryUnsupportedOperationException(String operation) {
		super(new CanaryError(CanaryError.Code.UNSUPPORTED_OPERATION, operation));
	}

}
