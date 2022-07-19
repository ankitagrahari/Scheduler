package com.dbtech.scheduler.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Job {

    private String jobName;
    private String apiURL;
    private String baseURL;
    private String cron;
}
