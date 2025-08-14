package com.example.quartzui.quartz.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SampleJobA implements Job {
	private static final Logger log = LoggerFactory.getLogger(SampleJobA.class);

	@Override
	public void execute(JobExecutionContext context) {
		log.info("SampleJobA executed. chainRunId={}", context.getMergedJobDataMap().getString("chainRunId"));
	}
}


