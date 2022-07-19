package com.dbtech.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Expose an API which will take the API of your choice and can schedule the job,
 * such that this Project will start calling the API of your choice after a certain
 * interval of time.
 *
 * This project can be directly used inside your microservices' framework.
 */
@SpringBootApplication
public class SchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }

}
