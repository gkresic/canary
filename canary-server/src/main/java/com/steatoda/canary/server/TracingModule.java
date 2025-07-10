package com.steatoda.canary.server;

import dagger.Module;
import dagger.Provides;
import io.micrometer.tracing.otel.bridge.*;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.ContextStorage;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.micrometer.tracing.Tracer;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import jakarta.inject.Singleton;

import java.util.Collections;

@Module
public interface TracingModule {

	@Provides
	@Singleton
	static OpenTelemetry provideOpenTelemetry() {

		// no need to report spans anywhere, yet
//		OtlpGrpcSpanExporter otlpExporter = OtlpGrpcSpanExporter.builder()
//			.setEndpoint("http://localhost:4317") // adjust to your OTLP endpoint
//			.build();

		SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
			.setSampler(Sampler.alwaysOn())
			//.addSpanProcessor(BatchSpanProcessor.builder(otlpExporter).build())
			.build();

		return OpenTelemetrySdk.builder()
			.setTracerProvider(sdkTracerProvider)
			.setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
			.build();

	}

	@Provides
	@Singleton
	static Tracer provideTracer(OpenTelemetry openTelemetry) {

		io.opentelemetry.api.trace.Tracer otelTracer = openTelemetry.getTracer("canary");

		OtelCurrentTraceContext otelCurrentTraceContext = new OtelCurrentTraceContext();

		Slf4JEventListener slf4JEventListener = new Slf4JEventListener();
		Slf4JBaggageEventListener slf4JBaggageEventListener = new Slf4JBaggageEventListener(Collections.emptyList());

		OtelTracer.EventPublisher eventPublisher = event -> {
			slf4JEventListener.onEvent(event);
			slf4JBaggageEventListener.onEvent(event);
		};

		ContextStorage.addWrapper(new EventPublishingContextWrapper(eventPublisher));

		return new OtelTracer(
			otelTracer,
			otelCurrentTraceContext,
			eventPublisher,
			new OtelBaggageManager(otelCurrentTraceContext, Collections.emptyList(), Collections.emptyList())
		);

	}

}
