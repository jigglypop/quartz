package com.example.quartzui.service;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.quartzui.job.JobStep;

@Component
public class SampleSteps {

	public JobStep stepHello() {
		return new JobStep() {
			@Override
			public String getName() { return "hello"; }

			@Override
			public void execute(Map<String, Object> context) {
				context.put("hello", "world");
			}
		};
	}

	public JobStep stepFailIfMissingHello() {
		return new JobStep() {
			@Override
			public String getName() { return "fail-if-missing-hello"; }

			@Override
			public void execute(Map<String, Object> context) throws Exception {
				if (!context.containsKey("hello")) {
					throw new IllegalStateException("hello key missing");
				}
			}
		};
	}
}


