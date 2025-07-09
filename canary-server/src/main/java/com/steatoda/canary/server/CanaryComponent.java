package com.steatoda.canary.server;

import jakarta.inject.Singleton;

import dagger.Component;

import com.steatoda.canary.server.rest.HelidonModule;

@Singleton
@Component(modules = {
	CanaryModule.class,
	HelidonModule.class,
})
public interface CanaryComponent {

	@Singleton
	Canary canary();

}
