package com.example.quartzui.quartz.listener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobExecutionListener implements JobListener {
	
	private final SimpMessagingTemplate messagingTemplate;
	
	public JobExecutionListener(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}
	
	@Override
	public String getName() {
		return "JobExecutionListener";
	}
	
	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		Map<String, Object> event = new HashMap<>();
		event.put("type", "JOB_TO_BE_EXECUTED");
		event.put("jobKey", context.getJobDetail().getKey().toString());
		event.put("jobGroup", context.getJobDetail().getKey().getGroup());
		event.put("jobName", context.getJobDetail().getKey().getName());
		event.put("fireInstanceId", context.getFireInstanceId());
		event.put("fireTime", context.getFireTime());
		event.put("scheduledFireTime", context.getScheduledFireTime());
		event.put("timestamp", new Date());
		
		messagingTemplate.convertAndSend("/topic/job-events", event);
	}
	
	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		Map<String, Object> event = new HashMap<>();
		event.put("type", "JOB_EXECUTION_VETOED");
		event.put("jobKey", context.getJobDetail().getKey().toString());
		event.put("jobGroup", context.getJobDetail().getKey().getGroup());
		event.put("jobName", context.getJobDetail().getKey().getName());
		event.put("fireInstanceId", context.getFireInstanceId());
		event.put("timestamp", new Date());
		
		messagingTemplate.convertAndSend("/topic/job-events", event);
	}
	
	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		Map<String, Object> event = new HashMap<>();
		event.put("type", "JOB_WAS_EXECUTED");
		event.put("jobKey", context.getJobDetail().getKey().toString());
		event.put("jobGroup", context.getJobDetail().getKey().getGroup());
		event.put("jobName", context.getJobDetail().getKey().getName());
		event.put("fireInstanceId", context.getFireInstanceId());
		event.put("fireTime", context.getFireTime());
		event.put("scheduledFireTime", context.getScheduledFireTime());
		event.put("jobRunTime", context.getJobRunTime());
		event.put("timestamp", new Date());
		
		if (jobException != null) {
			event.put("hasException", true);
			event.put("exceptionMessage", jobException.getMessage());
			event.put("exceptionType", jobException.getClass().getName());
		} else {
			event.put("hasException", false);
		}
		
		Object result = context.getResult();
		if (result != null) {
			event.put("result", result.toString());
		}
		
		messagingTemplate.convertAndSend("/topic/job-events", event);
	}
}
