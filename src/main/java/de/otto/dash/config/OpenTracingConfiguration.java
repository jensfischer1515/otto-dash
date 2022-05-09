package de.otto.dash.config;

import co.elastic.apm.opentracing.ElasticApmTracer;
import io.opentracing.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTracingConfiguration {

    @Bean
    public Tracer tracer() {
        return new ElasticApmTracer();
    }

}
