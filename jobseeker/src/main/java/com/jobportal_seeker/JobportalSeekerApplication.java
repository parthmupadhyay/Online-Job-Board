package com.jobportal_seeker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;


@EntityScan("com.models")
@ComponentScan(value = "com.controller")
@EnableJpaRepositories(value = "com.dao")
@ComponentScan(value = "com.utility")
@ComponentScan(value = "com.daoImpl")
@EnableAsync
@SpringBootApplication
public class JobportalSeekerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobportalSeekerApplication.class, args);
    }
}
