package com.steatoda.canary.server.rest;

import com.fasterxml.jackson.databind.json.JsonMapper;

import com.steatoda.canary.server.error.CanaryInvalidParameterException;
import com.steatoda.canary.server.rest.handler.CanaryErrorHandler;
import com.steatoda.canary.server.rest.param.BooleanDeserializer;
import com.steatoda.canary.server.rest.param.Deserializer;
import com.steatoda.canary.server.rest.param.InstantDeserializer;
import com.steatoda.canary.server.rest.param.IntegerDeserializer;
import com.steatoda.canary.server.rest.param.StringDeserializer;
import com.steatoda.canary.server.rest.param.UUIDDeserializer;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import io.helidon.common.media.type.MediaType;
import io.helidon.common.media.type.MediaTypes;
import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.helidon.http.DirectHandler;
import io.helidon.http.media.MediaContext;
import io.helidon.http.media.MediaContextConfig;
import io.helidon.http.media.jackson.JacksonSupport;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.DirectHandlers;
import io.helidon.webserver.http.HttpRouting;

import java.io.FileNotFoundException;
import java.io.InputStream;

import jakarta.inject.Singleton;

@Module
public interface HelidonModule {

	String ConfigFileName			= "helidon.yaml";
	MediaType ConfigFileMimeType	= MediaTypes.APPLICATION_X_YAML;

	@Provides
	@Singleton
	static Config provideConfig() {
		try (InputStream istream = Thread.currentThread().getContextClassLoader().getResourceAsStream(ConfigFileName)) {
			if (istream == null)
				throw new FileNotFoundException("Helidon configuration (" + ConfigFileName + ") not found on classpath");
			return Config.create(ConfigSources.create(istream, ConfigFileMimeType));
		} catch (Exception e) {
			throw new RuntimeException("Error loading Helidon configuration", e);
		}
	}

	@Provides
	@Singleton
	static MediaContext provideMediaContext(JsonMapper jsonMapper) {
		return MediaContextConfig.builder()
			.addMediaSupport(JacksonSupport.create(jsonMapper))
			.build();
	}

	@Provides
	@Singleton
	static WebServer provideHelidonWebServer(
		Config config,
		MediaContext mediaContext,
		DefaultRootRouter defaultRootRouter,
		PrivateRootRouter privateRootRouter,
		CanaryErrorHandler canaryErrorHandler
	) {

		// use CanaryDirectHandler for handling all "direct" events
		DirectHandlers.Builder directHandlersBuilder = DirectHandlers.builder();
		for (DirectHandler.EventType eventType : DirectHandler.EventType.values()) {
			directHandlersBuilder.addHandler(eventType, canaryErrorHandler);
		}

		return WebServer.builder()
			.config(config.get("server"))
			.mediaContext(mediaContext)
			.routing(HttpRouting.builder()
				.register(defaultRootRouter)
				.error(Throwable.class, canaryErrorHandler)
			)
			.routing("private", HttpRouting.builder()
				.register(privateRootRouter)
				.error(Throwable.class, canaryErrorHandler)
			)
			.directHandlers(directHandlersBuilder.build())
			.build();

	}

	@Provides
	@Singleton
	static Deserializer.ExceptionFactory provideDeserializerExceptionFactory() {
		return CanaryInvalidParameterException::new;
	}

	@Provides
	@IntoMap
	@ClassKey(BooleanDeserializer.class)
	static Deserializer<?> provideBooleanDeserializer(Deserializer.ExceptionFactory exceptionFactory) {
		return new BooleanDeserializer(exceptionFactory);
	}

	@Provides
	@IntoMap
	@ClassKey(InstantDeserializer.class)
	static Deserializer<?> provideInstantDeserializer(Deserializer.ExceptionFactory exceptionFactory) {
		return new InstantDeserializer(exceptionFactory);
	}

	@Provides
	@IntoMap
	@ClassKey(UUIDDeserializer.class)
	static Deserializer<?> provideUUIDDeserializer(Deserializer.ExceptionFactory exceptionFactory) {
		return new UUIDDeserializer(exceptionFactory);
	}

	@Provides
	@IntoMap
	@ClassKey(IntegerDeserializer.class)
	static Deserializer<?> provideIntDeserializer(Deserializer.ExceptionFactory exceptionFactory) {
		return new IntegerDeserializer(exceptionFactory);
	}

	@Provides
	@IntoMap
	@ClassKey(StringDeserializer.class)
	static Deserializer<?> provideStringDeserializer(Deserializer.ExceptionFactory exceptionFactory) {
		return new StringDeserializer(exceptionFactory);
	}

}
