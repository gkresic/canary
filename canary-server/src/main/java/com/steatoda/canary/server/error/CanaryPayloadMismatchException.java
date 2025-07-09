package com.steatoda.canary.server.error;

import java.text.MessageFormat;
import java.util.Locale;

public class CanaryPayloadMismatchException extends CanaryErrorException {

	public CanaryPayloadMismatchException(Object o1, Object o2) {
		super(new CanaryError(
			CanaryError.Code.PAYLOAD_MISMATCH,
			new MessageFormat("{0} vs {1}", Locale.ENGLISH).format(new Object[] { o1, o2 })
		));
	}

}
