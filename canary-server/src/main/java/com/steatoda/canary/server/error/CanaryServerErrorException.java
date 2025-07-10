package com.steatoda.canary.server.error;

public class CanaryServerErrorException extends CanaryErrorException {

	public static final CanaryError ERROR = new CanaryError(CanaryError.Code.SERVER_ERROR, "Ooops... internal server error");

	public CanaryServerErrorException(String message) {
		this(message, null);
	}
	
	public CanaryServerErrorException(String message, Throwable t) {
		super(ERROR);
		this.messageToLog = message;
		initCause(t);
	}

	public String messageToLog() { return messageToLog; }

	private final String messageToLog;

}
