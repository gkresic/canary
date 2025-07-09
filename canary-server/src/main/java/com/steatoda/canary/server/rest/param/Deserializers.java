package com.steatoda.canary.server.rest.param;

import jakarta.inject.Inject;

import java.util.Map;
import java.util.NoSuchElementException;

public class Deserializers {

	@Inject
	public Deserializers(Map<Class<?>, Deserializer<?>> deserializers, Deserializer.ExceptionFactory exceptionFactory) {
		this.deserializers = deserializers;
		this.exceptionFactory = exceptionFactory;
	}

	public BooleanDeserializer bool() { return get(BooleanDeserializer.class); }
	public InstantDeserializer instant() { return get(InstantDeserializer.class); }
	public UUIDDeserializer uuid() { return get(UUIDDeserializer.class); }
	public IntegerDeserializer integer() { return get(IntegerDeserializer.class); }
	public StringDeserializer string() { return get(StringDeserializer.class); }

	public <D extends Deserializer<?>> D get(Class<D> clazz) {

		Deserializer<?> deserializer = deserializers.get(clazz);

		if (deserializer == null)
			throw new NoSuchElementException("Deserializer " + clazz + " is not registered");

		try {
			return clazz.cast(deserializer);
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("Wrong deserializer instance mapped to " + clazz + ": " + deserializer.getClass(), e);
		}

	}

	public <E extends Enum<E>> EnumDeserializer<E> enumType(Class<E> enumClass) {
		// TODO see note on EnumDeserializer.Factory
		//return enumDeserializerFactory.create(fieldsClass);
		return new EnumDeserializer<>(exceptionFactory, enumClass);
	}

	private final Map<Class<?>, Deserializer<?>> deserializers;
	//private final EnumDeserializer.Factory enumDeserializerFactory;
	private final Deserializer.ExceptionFactory exceptionFactory;

}
