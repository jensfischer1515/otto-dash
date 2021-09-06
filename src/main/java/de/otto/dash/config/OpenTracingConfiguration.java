package de.otto.dash.config;

import co.elastic.apm.opentracing.ElasticApmTracer;
import io.opentracing.Tracer;
import io.opentracing.contrib.mongo.common.TracingCommandListener;
import io.opentracing.contrib.mongo.common.providers.OperationCollectionSpanNameProvider;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTracingConfiguration {

    @Bean
    public Tracer tracer() {
        return new ElasticApmTracer();
    }

    @Bean
    public MongoClientSettingsBuilderCustomizer tracingListenerMongoCustomizer(Tracer tracer) {
        var tracingListener = new TracingCommandListener.Builder(tracer)
                .withSpanNameProvider(new OperationCollectionSpanNameProvider())
                .build();
        return mongoSettings -> mongoSettings.addCommandListener(tracingListener);
    }
}
