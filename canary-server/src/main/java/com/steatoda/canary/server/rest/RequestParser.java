package com.steatoda.canary.server.rest;

import com.fasterxml.jackson.core.JsonParseException;
import com.steatoda.canary.server.error.CanaryPayloadErrorException;
import com.steatoda.canary.server.error.CanaryPayloadMissingException;
import com.steatoda.canary.server.rest.param.Deserializer;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.helidon.common.GenericType;
import io.helidon.common.parameters.Parameters;
import io.helidon.http.media.ReadableEntity;
import io.helidon.http.media.jackson.JacksonRuntimeException;
import io.helidon.webserver.http.ServerRequest;
import org.apache.commons.text.TextStringBuilder;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class RequestParser {

	@AssistedFactory
	public interface Factory {
		RequestParser create(ServerRequest request);
	}

	@AssistedInject
	public RequestParser(@Assisted ServerRequest request) {
		this.request = request;
	}

	public <T> T queryParam(String name, Deserializer<T> deserializer) {

		Parameters queryParameters = request.query();

		if (!queryParameters.contains(name))
			// reuse exception factory from deserializer
			return deserializer.fromString(name, null);

		return deserializer.fromString(name, queryParameters.get(name));

	}

	public <T> Optional<T> maybeQueryParam(String name, Deserializer<T> deserializer) {

		Parameters queryParameters = request.query();

		if (!queryParameters.contains(name))
			return Optional.empty();

		return Optional.of(deserializer.fromString(name, queryParameters.get(name)));

	}

	public <T> Stream<T> queryParams(String name, Deserializer<T> deserializer) {

		Parameters queryParameters = request.query();

		if (!queryParameters.contains(name))
			return Stream.empty();

		return queryParameters.all(name).stream().map(str -> deserializer.fromString(name, str));

	}

	public <T> T pathParam(String name, Deserializer<T> deserializer) {

		Parameters pathParameters = request.path().pathParameters();

		if (!pathParameters.contains(name))
			// reuse exception factory from deserializer
			return deserializer.fromString(name, null);

		return deserializer.fromString(name, pathParameters.get(name));

	}

	public <T> Optional<T> maybePathParam(String name, Deserializer<T> deserializer) {

		Parameters pathParameters = request.path().pathParameters();

		if (!pathParameters.contains(name))
			return Optional.empty();

		return deserializer.maybeFromString(name, pathParameters.get(name));

	}

	public <T> T body(Class<T> clazz) {
		return maybeBody(clazz).orElseThrow(CanaryPayloadMissingException::new);
	}

	public <T> T body(GenericType<T> type) {
		return maybeBody(type).orElseThrow(CanaryPayloadMissingException::new);
	}

	public <T> Optional<T> maybeBody(Class<T> clazz) {
		return maybeBody(entity -> entity.as(clazz));
	}

	public <T> Optional<T> maybeBody(GenericType<T> type) {
		return maybeBody(entity -> entity.as(type));
	}

	private <T> Optional<T> maybeBody(Function<ReadableEntity, T> func) {

		if (!request.content().hasEntity())
			return Optional.empty();

		try {
			return Optional.of(func.apply(request.content()));
		} catch (JacksonRuntimeException e) {
			TextStringBuilder strBuilder = new TextStringBuilder("Failed to deserialize JSON");
			if (e.getCause() instanceof JsonParseException jpe)
				strBuilder.appendSeparator(": ").append(jpe.getMessage());
			throw new CanaryPayloadErrorException(strBuilder.get());
		}
	}

	private final ServerRequest request;

}
