package com.steatoda.canary.server;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dagger.Binds;
import io.github.mweirauch.micrometer.jvm.extras.ProcessMemoryMetrics;
import io.github.mweirauch.micrometer.jvm.extras.ProcessThreadMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.micrometer.prometheusmetrics.PrometheusRenameFilter;
import jakarta.inject.Singleton;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import dagger.Module;
import dagger.Provides;

@Module
public interface CanaryModule {

	@Provides
	@Singleton
	static JsonMapper provideJsonMapper() {
		return JsonMapper.builder()
			.configure(MapperFeature.AUTO_DETECT_CREATORS, false)
			.configure(MapperFeature.AUTO_DETECT_FIELDS, false)
			.configure(MapperFeature.AUTO_DETECT_GETTERS, false)
			.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false)
			.configure(SerializationFeature.INDENT_OUTPUT, false)
			.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.serializationInclusion(JsonInclude.Include.NON_NULL)
			.addModule(new JavaTimeModule())
			.addModule(new Jdk8Module())
			.build();
	}

	@Binds
	@Singleton
	MeterRegistry provideMeterRegistry(CanaryMeterRegistry canaryMeterRegistry);

	@Binds
	@Singleton
	PrometheusMeterRegistry providePrometheusMeterRegistry(CanaryMeterRegistry canaryMeterRegistry);

	@Provides
	static CanaryStatus provideStatus() {
		CanaryStatus status = new CanaryStatus();
		status.setVersion(CanaryProperties.get().getVersion());
		return status;
	}

}
