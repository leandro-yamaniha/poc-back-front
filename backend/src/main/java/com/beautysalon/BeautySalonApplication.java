package com.beautysalon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BeautySalonApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeautySalonApplication.class, args);
    }
}
