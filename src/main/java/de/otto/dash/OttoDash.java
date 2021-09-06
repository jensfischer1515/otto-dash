package de.otto.dash;

import co.elastic.apm.attach.ElasticApmAttacher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
public class OttoDash {

    public static void main(String[] args) {
        System.setProperty("elastic.apm.service_version", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        ElasticApmAttacher.attach();
        SpringApplication.run(OttoDash.class, args);
    }
}
