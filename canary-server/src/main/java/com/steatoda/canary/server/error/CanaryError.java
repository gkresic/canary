package com.steatoda.canary.server.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Standard error model.
 */
public class CanaryError {

	public enum Code {
		SERVER_ERROR,
		UNSUPPORTED_OPERATION,
		ACCESS_DENIED,
		TIMEOUT,
		INVALID_PARAMETER,
		PAYLOAD_MISSING,
		PAYLOAD_MISMATCH,
		PAYLOAD_ERROR
	}

	@JsonCreator
	public CanaryError(@JsonProperty("code") Code code, @JsonProperty("message") String message) {
		this.code = code;
		this.message = message;
	}

	@JsonProperty
	public Code code() { return code; }
	@JsonProperty
	public String message() { return message; }

	@Override
	public String toString() { return code + ": " + message; }

	private final Code code;
	private final String message;

}
