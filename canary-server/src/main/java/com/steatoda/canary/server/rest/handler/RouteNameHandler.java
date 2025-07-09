package com.steatoda.canary.server.rest.handler;

import java.util.Optional;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import org.apache.commons.text.TextStringBuilder;

/**
 * Registers route name for logging purposes.
 */
public class RouteNameHandler implements Handler {

	@AssistedFactory
	public interface Factory {
		RouteNameHandler create(String name);
	}

	public static Optional<String> get(ServerRequest request) {

		return request.context().get(RouteNameHandler.class, TextStringBuilder.class).map(TextStringBuilder::get);

	}

	@AssistedInject
	public RouteNameHandler(@Assisted String name) {
		this.name = name;
	}

	@Override
	public void handle(ServerRequest request, ServerResponse response) {

		TextStringBuilder builder = request.context().get(RouteNameHandler.class, TextStringBuilder.class).orElseGet(() -> {
			TextStringBuilder newBuilder = new TextStringBuilder();
			request.context().register(RouteNameHandler.class, newBuilder);
			return newBuilder;
		});

		builder
			.appendSeparator('.')
			.append(name)
		;

		response.next();

	}

	private final String name;

}
