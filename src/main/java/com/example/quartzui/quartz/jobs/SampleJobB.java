package com.example.quartzui.quartz.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SampleJobB implements Job {
	private static final Logger log = LoggerFactory.getLogger(SampleJobB.class);

	@Override
	public void execute(JobExecutionContext context) {
		log.info("SampleJobB executed. chainRunId={}", context.getMergedJobDataMap().getString("chainRunId"));
	}
}


