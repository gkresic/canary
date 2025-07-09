package com.steatoda.canary.server.rest.param;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public abstract class Deserializer<T> {

	@FunctionalInterface
	public interface ExceptionFactory {
		RuntimeException create(String name, Object value, String details);
	}

	public Deserializer(ExceptionFactory exceptionFactory) {
		this.exceptionFactory = exceptionFactory;
	}

	public T fromString(String name, String value) {

		if (StringUtils.isBlank(value))
			throw exceptionFactory.create(name, value, "mandatory param is empty");

		try {
			return fromStringInternal(name, value);
		} catch (Exception e) {
			throw exceptionFactory.create(name, value, e.getMessage());
		}

	}

	public Optional<T> maybeFromString(String name, String value) {

		if (StringUtils.isBlank(value))
			return Optional.empty();

		try {
			return Optional.of(fromStringInternal(name, value));
		} catch (Exception e) {
			throw exceptionFactory.create(name, value, e.getMessage());
		}

	}

	private T fromStringInternal(String name, String value) {

		try {
			return deserialize(value);
		} catch (Exception e) {
			throw exceptionFactory.create(name, value, e.getMessage());
		}

	}

	protected abstract T deserialize(String value) throws Exception;

	private final ExceptionFactory exceptionFactory;

}
