package pl.feature.toggle.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class ToggleWriteServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToggleWriteServiceApplication.class, args);
    }

}
