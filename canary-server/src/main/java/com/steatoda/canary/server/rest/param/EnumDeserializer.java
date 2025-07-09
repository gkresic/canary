package com.steatoda.canary.server.rest.param;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

public class EnumDeserializer<E extends Enum<E>> extends Deserializer<E> {

	/**
	 * <p>@AssistedFactory does not currently support type parameters in the creator method.</p>
	 * <p>TODO when fixed, re-enable here and in {@link Deserializers}</p>
	 * @see <a href="https://github.com/google/dagger/issues/2279">GitHub issue</a>
 	 */
	//@AssistedFactory
	public interface Factory {
		<E extends Enum<E>> EnumDeserializer<E> create(Class<E> enumClass);
	}

	@AssistedInject
	public EnumDeserializer(ExceptionFactory exceptionFactory, @Assisted Class<E> enumClass) {
		super(exceptionFactory);
		this.enumClass = enumClass;
	}

	@Override
	protected E deserialize(String value) {
		return Enum.valueOf(enumClass, value);
	}

	private final Class<E> enumClass;

}
