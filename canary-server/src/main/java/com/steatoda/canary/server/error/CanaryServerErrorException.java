package com.steatoda.canary.server.error;

public class CanaryServerErrorException extends CanaryErrorException {

	public CanaryServerErrorException(String message) {
		this(message, null);
	}
	
	public CanaryServerErrorException(String message, Throwable t) {
		super(new CanaryError(CanaryError.Code.SERVER_ERROR, "Ooops... internal server error"));
		this.messageToLog = message;
		initCause(t);
	}

	public String messageToLog() { return messageToLog; }

	private final String messageToLog;

}
