package de.otto.tracing.tracingdemoservice;

import co.elastic.apm.opentracing.ElasticApmTracer;
import co.elastic.apm.attach.ElasticApmAttacher;
import io.opentracing.Tracer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TracingDemoServiceApplication {

	public static void main(String[] args) {
		ElasticApmAttacher.attach();
		SpringApplication.run(TracingDemoServiceApplication.class, args);
	}

	@Bean
	public Tracer tracer() {
		return new ElasticApmTracer();
	}
}
