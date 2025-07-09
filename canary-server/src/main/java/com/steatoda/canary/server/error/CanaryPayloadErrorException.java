package com.steatoda.canary.server.error;

public class CanaryPayloadErrorException extends CanaryErrorException {

	public CanaryPayloadErrorException(String message) {
		super(new CanaryError(CanaryError.Code.PAYLOAD_ERROR, message));
	}

}
