package com.steatoda.canary.server.error;

import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Locale;

public class CanaryInvalidParameterException extends CanaryErrorException {

	public CanaryInvalidParameterException(String name, Object value, String details) {
		super(new CanaryError(CanaryError.Code.INVALID_PARAMETER, format(name, value, details)));
		this.name = name;
		this.value = value;
		this.details = details;
	}

	public String name() { return name; }
	public Object value() { return value; }
	public String details() { return details; }

	private static String format(String name, Object value, String details) {
		StringBuilder messageBuilder = new StringBuilder("Invalid value for parameter '" + name + "': '" + value + "'");
		if (!StringUtils.isBlank(details))
			messageBuilder.append(" (").append(details).append(")");
		return messageBuilder.toString();
	}

	private final String name;
	private final Object value;
	private final String details;

}
