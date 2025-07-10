package com.steatoda.canary.server.rest.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.steatoda.canary.server.error.CanaryErrorException;
import com.steatoda.canary.server.error.CanaryServerErrorException;
import com.steatoda.canary.server.error.CanaryUnsupportedOperationException;
import io.helidon.common.media.type.MediaTypes;
import io.helidon.http.*;
import io.helidon.webserver.http.ErrorHandler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class CanaryErrorHandler implements ErrorHandler<Throwable>, DirectHandler {

	@Inject
	public CanaryErrorHandler(Tracer tracer, JsonMapper jsonMapper) {
		this.tracer = tracer;
		this.jsonMapper = jsonMapper;
	}

	@Override
	public void handle(ServerRequest request, ServerResponse response, Throwable throwable) {

		reportToTracer(request, throwable);

		CanaryErrorException cee = mapToCanaryErrorException(request, throwable);

		// internal server errors deserve a special treatment
		if (throwable instanceof CanaryServerErrorException ce) {
			logError("API internal server error: " + ce.messageToLog(), request, ce);
			send(response, ce);
			return;
		}

		LOG.warn("API error {}", cee.error());

		send(response, cee);

	}

	@Override
	public TransportResponse handle(
		TransportRequest request,
		EventType eventType,
		Status defaultStatus,
		ServerResponseHeaders responseHeaders,
		Throwable throwable,
		System.Logger logger
	) {
		LOG.error("Direct error of type {} (check next log for more details)", eventType, throwable);
		return handle(request, eventType, defaultStatus, responseHeaders, throwable.getMessage());
	}

	@Override
	public TransportResponse handle(
		TransportRequest request,
		EventType eventType,
		Status defaultStatus,
		ServerResponseHeaders responseHeaders,
		String message
	) {

		LOG.error(
			"Direct error of type {} while processing {} @ {}: {} (check previous log for stacktrace)",
			eventType,
			request.method(),
			request.path(),
			message
		);

		String errorResponse;
		try {
			errorResponse = jsonMapper.writeValueAsString(CanaryServerErrorException.ERROR);
		} catch (JsonProcessingException e) {
			LOG.error("Can't even serialize error to JSON!?", e);
			errorResponse =
			"""
			{
				"code": "SERVER_ERROR",
				"message": "Ooops... internal server error"
			}
			""";
		}

		return TransportResponse.builder()
			.status(Status.INTERNAL_SERVER_ERROR_500)
			.header(HeaderValues.create(HeaderNames.CONTENT_TYPE, MediaTypes.APPLICATION_JSON.text()))
			.entity(errorResponse)
			.build();

	}

	private void reportToTracer(ServerRequest request, Throwable throwable) {

		Span currentSpan = tracer.currentSpan();

		if (currentSpan == null) {
			logError("REST request without a span", request, throwable);
			return;
		}

		currentSpan.error(throwable);

	}

	private CanaryErrorException mapToCanaryErrorException(ServerRequest request, Throwable throwable) {

		// return CanaryErrorException(s) verbatim
		if (throwable instanceof CanaryErrorException cee)
			return cee;

		// map well-known and expected  non-CanaryErrorException(s)

		if (throwable instanceof NotFoundException)
			return new CanaryUnsupportedOperationException(endpointIdentifier(request));

		// wrap any unknowns in CanaryServerErrorException

		return new CanaryServerErrorException("Unknown exception thrown during API routing", throwable);

	}

	private void logError(String message, ServerRequest request, Throwable throwable) {
		LOG.error("{}: {}", message, endpointIdentifier(request), throwable);
	}

	private void send(ServerResponse response, CanaryErrorException ce) {
		response
			.status(httpStatus(ce))
			.send(ce.error());
	}

    private Status httpStatus(CanaryErrorException ce) {
		return switch (ce.error().code()) {
            case SERVER_ERROR			-> Status.INTERNAL_SERVER_ERROR_500;
            case UNSUPPORTED_OPERATION	-> Status.PRECONDITION_FAILED_412;
            case ACCESS_DENIED			-> Status.FORBIDDEN_403;
            case TIMEOUT				-> Status.REQUEST_TIMEOUT_408;
			case INVALID_PARAMETER		-> Status.BAD_REQUEST_400;
            case PAYLOAD_MISSING		-> Status.BAD_REQUEST_400;
            case PAYLOAD_MISMATCH		-> Status.BAD_REQUEST_400;
            case PAYLOAD_ERROR			-> Status.BAD_REQUEST_400;
        };
    }

	private String endpointIdentifier(ServerRequest request) {
		return String.format("%s @ %s", request.prologue().method().text(), request.path().absolute().path());
	}

    private static final Logger LOG = LoggerFactory.getLogger(CanaryErrorHandler.class);

	private final Tracer tracer;
	private final JsonMapper jsonMapper;

}
