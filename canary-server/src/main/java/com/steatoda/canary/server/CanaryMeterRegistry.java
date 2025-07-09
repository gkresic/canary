package com.steatoda.canary.server;

import io.github.mweirauch.micrometer.jvm.extras.ProcessMemoryMetrics;
import io.github.mweirauch.micrometer.jvm.extras.ProcessThreadMetrics;
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
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class CanaryMeterRegistry extends PrometheusMeterRegistry {

	@Inject
	public CanaryMeterRegistry() {

		super(PrometheusConfig.DEFAULT);

		config().meterFilter(new PrometheusRenameFilter());

		new ClassLoaderMetrics().bindTo(this);
		new JvmMemoryMetrics().bindTo(this);
		(jvmGcMetricsMetrics = new JvmGcMetrics()).bindTo(this);
		new ProcessorMetrics().bindTo(this);
		new JvmThreadMetrics().bindTo(this);
		(logbackMetrics = new LogbackMetrics()).bindTo(this);
		new FileDescriptorMetrics().bindTo(this);
		new UptimeMetrics().bindTo(this);

		new ProcessMemoryMetrics().bindTo(this);
		new ProcessThreadMetrics().bindTo(this);

		// TODO app metrics calculated on demand
//		new UserMetrics().bindTo(this);

	}

	@Override
	public void close() {
		jvmGcMetricsMetrics.close();
		logbackMetrics.close();
		super.close();
	}

	private final JvmGcMetrics jvmGcMetricsMetrics;
	private final LogbackMetrics logbackMetrics;

}
