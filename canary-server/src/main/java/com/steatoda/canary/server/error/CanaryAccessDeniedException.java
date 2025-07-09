package com.steatoda.canary.server.error;

public class CanaryAccessDeniedException extends CanaryErrorException {

	public CanaryAccessDeniedException(String operation) {
		super(new CanaryError(CanaryError.Code.ACCESS_DENIED, operation));
	}

}
