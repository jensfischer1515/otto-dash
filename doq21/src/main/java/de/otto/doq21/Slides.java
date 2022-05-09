package de.otto.doq21;

import co.elastic.apm.api.CaptureSpan;
import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Transaction;
import co.elastic.apm.attach.ElasticApmAttacher;
import co.elastic.apm.opentracing.ElasticApmTracer;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.contrib.mongo.common.TracingCommandListener;
import io.opentracing.contrib.mongo.common.providers.OperationCollectionSpanNameProvider;
import io.opentracing.tag.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

public class Slides {












/*
!               otto group
!               ██████   ██████   ██████      ██████   ██
!               ██   ██ ██    ██ ██    ██          ██ ███
!               ██   ██ ██    ██ ██    ██      █████   ██
!               ██   ██ ██    ██ ██ ▄▄ ██     ██       ██
!               ██████   ██████   ██████      ███████  ██
!                                    ▀▀
!                 c   o   n   f   e   r   e   n   c   e

*    ██████ ███████ ██             ██████  ████████ ████████  ██████
*   ██      ██      ██     ██     ██    ██    ██       ██    ██    ██
*   ██      ███████ ██            ██    ██    ██       ██    ██    ██
*   ██           ██ ██     ██     ██    ██    ██       ██    ██    ██
*    ██████ ███████ ██             ██████     ██       ██     ██████
*
*         ======================================================
*         # From Zero to Distributed Tracing in five minutes!  #
*         # Jens Fischer <jens.fischer@otto.de> @jensfischerhh #
*         ======================================================



























?    ██████  ██████  ███████ ███    ██ ████████ ██████   █████   ██████ ██ ███    ██  ██████
?   ██    ██ ██   ██ ██      ████   ██    ██    ██   ██ ██   ██ ██      ██ ████   ██ ██
?   ██    ██ ██████  █████   ██ ██  ██    ██    ██████  ███████ ██      ██ ██ ██  ██ ██   ███
?   ██    ██ ██      ██      ██  ██ ██    ██    ██   ██ ██   ██ ██      ██ ██  ██ ██ ██    ██
?    ██████  ██      ███████ ██   ████    ██    ██   ██ ██   ██  ██████ ██ ██   ████  ██████
?
?       .............
?      -=============:      > per-process logging and metric monitoring have their place,                  
?     .:::::-===:::===:     > but neither can reconstruct the elaborate journeys that transactions         
?           .===   .===-    > take as they propagate across a distributed system.                          
?   =++=.   .===    .-==-   > Distributed traces are these journeys.                                      
?  =++=     .===                                                                                           
? -+++:     .===      .:::.     https://medium.com/opentracing/5f4297d1736
?  -+++:    .===     :=++-      Ben Sigelman
?   :+++-   .:::    -+++:       Co-founder and CEO at LightStep,
?    .=++-         -++=.        Co-creator of @OpenTelemetry and @OpenTracing,
?     .=+++++++++++++=.         built Dapper (Google’s tracing system).
?       -------------


























██   ██ ██ ██████   █████  ███    ██  █████
██  ██  ██ ██   ██ ██   ██ ████   ██ ██   ██
█████   ██ ██████  ███████ ██ ██  ██ ███████
██  ██  ██ ██   ██ ██   ██ ██  ██ ██ ██   ██
██   ██ ██ ██████  ██   ██ ██   ████ ██   ██

https://kibana-ft1.live.logmon.cloud.otto.de/app/apm/services/order-core/service-map?rangeFrom=now-8h&rangeTo=now&environment=live



























██████  ██████   ██████  ██████   █████   ██████   █████  ████████ ██  ██████  ███    ██
██   ██ ██   ██ ██    ██ ██   ██ ██   ██ ██       ██   ██    ██    ██ ██    ██ ████   ██
██████  ██████  ██    ██ ██████  ███████ ██   ███ ███████    ██    ██ ██    ██ ██ ██  ██
██      ██   ██ ██    ██ ██      ██   ██ ██    ██ ██   ██    ██    ██ ██    ██ ██  ██ ██
██      ██   ██  ██████  ██      ██   ██  ██████  ██   ██    ██    ██  ██████  ██   ████

W3C Recommendation https://www.w3.org/TR/trace-context-1/

?>>> GET https://some-host/some-path HTTP/1.1
?>>> Accept: text/html
?>>> traceparent: 00-63ff76eb8eb64706992ccd42574c1396-2c769e27569dbb37-01
?>>> tracestate: es=s:1

traceparent:
vv-tttttttttttttttttttttttttttttttt-pppppppppppppppp-ff
*v: version          t: trace-id
*f: flags            p: parent-id

tracestate:
v+=x+
*v: vendor-key       x: vendor-specific state



























 ██████  ████████ ████████  ██████      ███████  ██████      █████  ██████  ██
██    ██    ██       ██    ██    ██     ██      ██          ██   ██ ██   ██ ██
██    ██    ██       ██    ██    ██     █████   ██          ███████ ██████  ██
██    ██    ██       ██    ██    ██     ██      ██          ██   ██ ██      ██
 ██████     ██       ██     ██████      ███████  ██████     ██   ██ ██      ██

https://api.develop.otto.de/portal/

Product API  : Team Opal
Checkout API : Team FT1

===============================================================================
=== OAuth2 Token Exchange
===============================================================================

?>>> POST https://api.develop.otto.de/oauth2/token HTTP/1.1
?>>> Content-Type: application/x-www-form-urlencoded;charset=UTF-8
?>>>
?>>> grant_type=client_credentials &
?>>> client_id=<client_id> &
?>>> client_secret=<client_secret> &
?>>> scope=checkouts.write+opal.products.read

*<<< HTTP/1.1 200 OK
*<<< Content-Type: application/json;charset=UTF-8
*<<<
*<<< {"access_token":"<JWT>","expires_in":299,"scope":"checkouts.write opal.products.read","token_type":"bearer"}

===============================================================================
=== REST API Call
===============================================================================

?>>> GET https://api.develop.otto.de/products HTTP/1.1
?>>> Accept: application/hal+json
?>>> Authorization: Bearer <JWT>

*<<< HTTP/1.1 200 OK
*<<< Content-Type: application/hal+json
*<<<
*<<< {"_links":{"curies":[{"href":"http://spec.otto.de/link-relations/{rel}","templated":true,"name":"o"}],"o:product":[{"href":"/opal-product/products/603656900","type":"application/hal+json","title":"Sigikid Aktiv-Anhänger Wald, PlayQ Wuller Wullawoods (41505...



























!    ██████  ████████ ████████  ██████        ██████   █████  ███████ ██   ██
!   ██    ██    ██       ██    ██    ██       ██   ██ ██   ██ ██      ██   ██
!   ██    ██    ██       ██    ██    ██ █████ ██   ██ ███████ ███████ ███████
!   ██    ██    ██       ██    ██    ██       ██   ██ ██   ██      ██ ██   ██
!    ██████     ██       ██     ██████        ██████  ██   ██ ███████ ██   ██
!                      Web application based on Spring Boot
!                   https://github.com/jensfischer1515/otto-dash

* Model       : Java 16 records
* View        : Thymeleaf, Bootstrap
* Controller  : Spring Web MVC
* Persistence : Spring Data MongoDB
* API Client  : Spring RestTemplate, Apache HttpComponents, Caffeine
* Logging     : Slf4J, Logback, Logbook

http://otto-dash.localtest.me:8080/



























     ██  █████  ██    ██  █████       ██  ██████
     ██ ██   ██ ██    ██ ██   ██     ███ ██
     ██ ███████ ██    ██ ███████      ██ ███████
██   ██ ██   ██  ██  ██  ██   ██      ██ ██    ██
 █████  ██   ██   ████   ██   ██      ██  ██████
*/

    public static class MyPojo {
        private int id;
        private String name;

        public MyPojo() {
        }

        public MyPojo(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MyPojo myPojo = (MyPojo) o;
            return Objects.equals(id, myPojo.id) && Objects.equals(name, myPojo.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }

        @Override
        public String toString() {
            return "MyPojo{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public record MyRecord(
            int id,
            String name
    ) {
        void typesAndVars() {
            var myPojo = new MyPojo(1, "name");
            var myRecord = new MyRecord(1, "name");
            assert myPojo.getId() == myRecord.id();
            //myRecord.name("immutable");
        }
    }



























/*

███████ ██       █████  ███████ ████████ ██  ██████      █████  ██████  ███    ███
██      ██      ██   ██ ██         ██    ██ ██          ██   ██ ██   ██ ████  ████
█████   ██      ███████ ███████    ██    ██ ██          ███████ ██████  ██ ████ ██
██      ██      ██   ██      ██    ██    ██ ██          ██   ██ ██      ██  ██  ██
███████ ███████ ██   ██ ███████    ██    ██  ██████     ██   ██ ██      ██      ██

https://www.elastic.co/guide/en/apm/agent/java/current/index.html
https://www.elastic.co/guide/en/apm/agent/java/current/supported-technologies-details.html



























██████  ███████ ██████  ███████ ███    ██ ██████  ███████ ███    ██  ██████ ██ ███████ ███████
██   ██ ██      ██   ██ ██      ████   ██ ██   ██ ██      ████   ██ ██      ██ ██      ██
██   ██ █████   ██████  █████   ██ ██  ██ ██   ██ █████   ██ ██  ██ ██      ██ █████   ███████
██   ██ ██      ██      ██      ██  ██ ██ ██   ██ ██      ██  ██ ██ ██      ██ ██           ██
██████  ███████ ██      ███████ ██   ████ ██████  ███████ ██   ████  ██████ ██ ███████ ███████

build.gradle:

?   dependencies {
?       ...
?       implementation 'co.elastic.apm:apm-agent-attach:1.30.1'
?       ...
?   }



























 █████  ██████  ███    ███      █████   ██████  ███████ ███    ██ ████████
██   ██ ██   ██ ████  ████     ██   ██ ██       ██      ████   ██    ██
███████ ██████  ██ ████ ██     ███████ ██   ███ █████   ██ ██  ██    ██
██   ██ ██      ██  ██  ██     ██   ██ ██    ██ ██      ██  ██ ██    ██
██   ██ ██      ██      ██     ██   ██  ██████  ███████ ██   ████    ██

*/

    @SpringBootApplication
    public class MySpringBootApp {

        public static void main(String[] args) {
            ElasticApmAttacher.attach();
            SpringApplication.run(MySpringBootApp.class, args);
        }
    }

/*

























 █████  ██████  ███    ███      ██████  ██████  ███    ██ ███████ ██  ██████
██   ██ ██   ██ ████  ████     ██      ██    ██ ████   ██ ██      ██ ██
███████ ██████  ██ ████ ██     ██      ██    ██ ██ ██  ██ █████   ██ ██   ███
██   ██ ██      ██  ██  ██     ██      ██    ██ ██  ██ ██ ██      ██ ██    ██
██   ██ ██      ██      ██      ██████  ██████  ██   ████ ██      ██  ██████

https://www.elastic.co/guide/en/apm/agent/java/current/configuration.html#_option_reference

Command Line:

?    java -jar otto-dash.jar \
?       -Delastic.apm.server_urls=https://<your-cluster-address> \
?       -Delastic.apm.secret_token=<your-token>

src/main/resources/elasticapm.properties:

?   service_name=otto-dash
?   service_version=0.1-localbuild
?   environment=local
?   application_packages=de.otto.dash
?   global_labels=org=otto-ec,team=otto-dash
?
?   enabled=true
?   instrument=true
?   recording=true
?   transaction_sample_rate=1.0
?   metrics_interval=1s
?
?   # SLF4J/Logback integration
?   enable_log_correlation=true




























!                           ██████   ██████  ███    ██ ███████ ██
!                           ██   ██ ██    ██ ████   ██ ██      ██
!                           ██   ██ ██    ██ ██ ██  ██ █████   ██
!                           ██   ██ ██    ██ ██  ██ ██ ██
!                           ██████   ██████  ██   ████ ███████ ██

                                            kthxbye!



























███████ ███████ ██████  ██    ██ ██  ██████ ███████     ██    ██ ███████ ██████  ███████ ██  ██████  ███    ██
██      ██      ██   ██ ██    ██ ██ ██      ██          ██    ██ ██      ██   ██ ██      ██ ██    ██ ████   ██
███████ █████   ██████  ██    ██ ██ ██      █████       ██    ██ █████   ██████  ███████ ██ ██    ██ ██ ██  ██
     ██ ██      ██   ██  ██  ██  ██ ██      ██           ██  ██  ██      ██   ██      ██ ██ ██    ██ ██  ██ ██
███████ ███████ ██   ██   ████   ██  ██████ ███████       ████   ███████ ██   ██ ███████ ██  ██████  ██   ████

See each deployment as a new green bubble in Kibana
https://kibana-ft1.live.logmon.cloud.otto.de/app/apm/services/checkout-core/transactions/view?kuery=&rangeFrom=now-3d&rangeTo=now&environment=develop&transactionName=CheckoutController%23loadCheckout

 */

    public static void main(String[] args) {
        System.setProperty("elastic.apm.service_version", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        ElasticApmAttacher.attach();
        SpringApplication.run(MySpringBootApp.class, args);
    }

    /*



























 ██████ ██    ██ ███████ ████████  ██████  ███    ███     ███████ ██████   █████  ███    ██ ███████
██      ██    ██ ██         ██    ██    ██ ████  ████     ██      ██   ██ ██   ██ ████   ██ ██
██      ██    ██ ███████    ██    ██    ██ ██ ████ ██     ███████ ██████  ███████ ██ ██  ██ ███████
██      ██    ██      ██    ██    ██    ██ ██  ██  ██          ██ ██      ██   ██ ██  ██ ██      ██
 ██████  ██████  ███████    ██     ██████  ██      ██     ███████ ██      ██   ██ ██   ████ ███████

Elastic APM Java agent public API
https://www.elastic.co/guide/en/apm/agent/java/current/public-api.html

build.gradle:

?   dependencies {
?       ...
?       implementation 'co.elastic.apm:apm-agent-api:1.30.1'
?       ...
?   }

*/
    @CaptureSpan(value = "Do something important")
    public void doSomethingImportant() {
        Transaction transaction = ElasticApm.currentTransaction();
        transaction.setLabel("customLabelKey", "customLabelValue");

        // ... your code here
    }
/*



    build.gradle:

    ?   dependencies {
    ?       ...
    ?       implementation 'co.elastic.apm:apm-opentracing:1.30.1'
    ?       ...
    ?   }
 */

    @Configuration
    public class OpenTracingConfiguration {
        @Bean
        public Tracer tracer() {
            return new ElasticApmTracer();
        }
    }

    @Component
    public class MyService {
        @Autowired
        private Tracer tracer;

        public void doSomethingImportant() {
            Span span = tracer
                    .buildSpan("Do something important")
                    .withTag(Tags.COMPONENT, "api")
                    .withTag(Tags.SPAN_KIND, Tags.SPAN_KIND_CLIENT)
                    .withTag(Tags.HTTP_URL, "https://api.develop.otto.de")
                    .withTag(Tags.HTTP_METHOD, "POST")
                    .withTag("customLabelKey", "customLabelValue")
                    .start();

            // ... your code here
            boolean error = true; // depending on your logic

            if (error) {
                span.setTag(Tags.ERROR, true);
                span.setTag(Tags.HTTP_STATUS, 500);
                span.log(Collections.singletonMap("event", "it failed"));
                span.setBaggageItem("feature", "not yet supported");
            }

            span.finish();
        }
    }

    /*

























███    ███  ██████  ███    ██  ██████   ██████  ██████  ██████
████  ████ ██    ██ ████   ██ ██       ██    ██ ██   ██ ██   ██
██ ████ ██ ██    ██ ██ ██  ██ ██   ███ ██    ██ ██   ██ ██████
██  ██  ██ ██    ██ ██  ██ ██ ██    ██ ██    ██ ██   ██ ██   ██
██      ██  ██████  ██   ████  ██████   ██████  ██████  ██████

3rd-Party OpenTracing API Contributions
https://github.com/opentracing-contrib/java-mongo-driver

build.gradle:

?   dependencies {
?       ...
?       implementation 'io.opentracing.contrib:opentracing-mongo-driver:0.1.5'
?       ...
?   }
*/

    @Bean
    public MongoClientSettingsBuilderCustomizer tracingListenerMongoCustomizer(Tracer tracer) {
        var tracingListener = new TracingCommandListener.Builder(tracer)
                .withSpanNameProvider(new OperationCollectionSpanNameProvider())
                .build();
        return mongoSettings -> mongoSettings.addCommandListener(tracingListener);
    }

/*

























!            ██████       █████  ███    ██ ██████       █████
!           ██    ██     ██   ██ ████   ██ ██   ██     ██   ██
!           ██    ██     ███████ ██ ██  ██ ██   ██     ███████
!           ██ ▄▄ ██     ██   ██ ██  ██ ██ ██   ██     ██   ██
!            ██████      ██   ██ ██   ████ ██████      ██   ██
!               ▀▀
                                Questions?
                                Comments?
                                Feedback?


























!   ████████ ██   ██  █████  ███    ██ ██   ██     ██    ██  ██████  ██    ██
!      ██    ██   ██ ██   ██ ████   ██ ██  ██       ██  ██  ██    ██ ██    ██
!      ██    ███████ ███████ ██ ██  ██ █████         ████   ██    ██ ██    ██
!      ██    ██   ██ ██   ██ ██  ██ ██ ██  ██         ██    ██    ██ ██    ██
!      ██    ██   ██ ██   ██ ██   ████ ██   ██        ██     ██████   ██████

*    https://github.com/jensfischer1515/otto-dash

    Tools I used for these slides
    https://plugins.jetbrains.com/plugin/12895-comments-highlighter
    https://patorjk.com/software/taag/#p=display&f=ANSI%20Regular&t=doq21
    https://ascii-generator.site/
 */


}
