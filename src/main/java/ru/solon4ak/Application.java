package ru.solon4ak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import ru.solon4ak.configuration.MVCConfig;
import ru.solon4ak.configuration.SecurityConfiguration;

@SpringBootApplication
@EntityScan( basePackages = {"ru.solon4ak.model"})
@Import({SecurityConfiguration.class, MVCConfig.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
