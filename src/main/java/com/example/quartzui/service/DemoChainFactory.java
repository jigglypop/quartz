package com.example.quartzui.service;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import com.example.quartzui.job.ChainJobDefinition;
import com.example.quartzui.job.DbLoggingJobStep;
import com.example.quartzui.mapper.JobLogMapper;

@Component
public class DemoChainFactory {
	private final SampleSteps sampleSteps;
	private final JobLogMapper jobLogMapper;

	public DemoChainFactory(SampleSteps sampleSteps, JobLogMapper jobLogMapper) {
		this.sampleSteps = sampleSteps;
		this.jobLogMapper = jobLogMapper;
	}

	public ChainJobDefinition sampleChainSimple() {
		return new ChainJobDefinition(
				"demo",
				Arrays.asList(
						sampleSteps.stepHello(),
						sampleSteps.stepFailIfMissingHello()
				)
		);
	}

	public ChainJobDefinition sampleChainWithDb() {
		return new ChainJobDefinition(
				"demo-db",
				Arrays.asList(
						sampleSteps.stepHello(),
						new DbLoggingJobStep(jobLogMapper, "db-log", "logged"),
						sampleSteps.stepFailIfMissingHello()
				)
		);
	}
}


