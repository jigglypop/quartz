package com.example.quartzui.job.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.quartzui.job.JobStep;

@Component
public class SkipAndCollectErrorPolicy implements JobErrorPolicy {
	private static final Logger log = LoggerFactory.getLogger(SkipAndCollectErrorPolicy.class);

	@Override
	@SuppressWarnings("unchecked")
	public boolean handle(JobStep step, int stepIndex, Exception exception, Map<String, Object> context) {
		List<String> errors = (List<String>) context.computeIfAbsent("errors", k -> new ArrayList<String>());
		errors.add("step=" + (stepIndex + 1) + ", name=" + step.getName() + ", error=" + exception.toString());
		log.warn("Skip step {} due to error: {}", step.getName(), exception.toString());
		return false; // continue chain
	}
}


