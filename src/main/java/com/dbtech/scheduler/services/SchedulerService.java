package com.dbtech.scheduler.services;

import com.dbtech.scheduler.entity.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Service
public class SchedulerService {

    private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);
    private TaskScheduler taskScheduler;
    private WebClient webClient;
    private Map<String, Job> schedulerMap = new HashMap<>();

    @Autowired
    SchedulerService(TaskScheduler taskScheduler, WebClient webClient){
        this.taskScheduler = taskScheduler;
        this.webClient = webClient;
    }

    public Map<String, Job> getScheduledJobs(){
        return schedulerMap;
    }
    
    public void deleteScheduledJob(String jobId) {
        if(schedulerMap.containsKey(jobId))
            schedulerMap.remove(jobId);
        else
            log.error("No jobs exists with Id:"+ jobId);
    }
    
    public void scheduleGetJob(Job job) {
        log.info("scheduling get job:"+ job.toString());
        taskScheduler.schedule(
                () -> {
                    ResponseEntity<String> response = webClient.get()
                            .uri(job.getBaseURL() + job.getApiURL())
                            .header("Content-Type", "application/json")
                            .retrieve()
                            .toEntity(String.class)
                            .block();
                    if(response.getStatusCode().isError()){
                        log.error("Failure calling Job:"+ job.getJobName()+ "with URL:"+ job.getBaseURL()+job.getApiURL());
                    } else {
                        log.info(response.getBody());
                    }
                },
                new CronTrigger(job.getCron(), TimeZone.getTimeZone(TimeZone.getDefault().toZoneId()))
        );
        schedulerMap.put(job.getJobName() + "-" + System.currentTimeMillis(), job);
    }
    
    public void schedulePostJob(Job job){
        log.info("scheduling post job:"+ job.toString());
        taskScheduler.schedule(
                () -> {
                    ResponseEntity<String> response = webClient.post()
                            .uri(job.getBaseURL() + job.getApiURL())
                            .header("Content-Type", "application/json")
                            .bodyValue(job)
                            .retrieve()
                            .toEntity(String.class)
                            .block();
    
                    if(response.getStatusCode().isError()){
                        log.error("Failure calling Job:"+ job.getJobName()+ "with URL:"+ job.getBaseURL()+job.getApiURL());
                    } else {
                        log.info(response.getBody());
                    }
                },
                new CronTrigger(job.getCron(), TimeZone.getTimeZone(TimeZone.getDefault().toZoneId()))
        );
        schedulerMap.put(job.getJobName() + "-" + System.currentTimeMillis(), job);
    }
}
