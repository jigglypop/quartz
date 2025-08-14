package com.example.quartzui.job.error;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.quartzui.job.JobStep;

@Component
public class StopOnFirstErrorPolicy implements JobErrorPolicy {
	private static final Logger log = LoggerFactory.getLogger(StopOnFirstErrorPolicy.class);

	@Override
	public boolean handle(JobStep step, int stepIndex, Exception exception, Map<String, Object> context) {
		log.error("Error at step {} ({}): {}", stepIndex + 1, step.getName(), exception.toString());
		return true; // stop chain
	}
}


