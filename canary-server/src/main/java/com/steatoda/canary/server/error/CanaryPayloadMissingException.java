package com.steatoda.canary.server.error;

public class CanaryPayloadMissingException extends CanaryErrorException {

	public CanaryPayloadMissingException() {
		super(new CanaryError(CanaryError.Code.PAYLOAD_MISSING, "Payload is missing"));
	}

}
