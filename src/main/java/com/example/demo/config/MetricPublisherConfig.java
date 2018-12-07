package com.example.demo.config;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;

public class MetricPublisherConfig {
	public static CollectorRegistry registry = CollectorRegistry.defaultRegistry;

    // register prometheus
    private final Gauge processingTime = Gauge.build().namespace("my_service").name("transaction").labelNames("service", "status_code", "isexpect").help("Hello").register(registry);

    private static MetricPublisherConfig INSTANCE = new MetricPublisherConfig();

    public static MetricPublisherConfig getInstance() {
        return INSTANCE;
    }

    public Gauge getProcessingTimeGauge() {
        return processingTime;
    }
}
