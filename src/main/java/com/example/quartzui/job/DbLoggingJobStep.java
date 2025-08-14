package com.example.quartzui.job;

import java.util.Map;

import com.example.quartzui.mapper.JobLogMapper;

public class DbLoggingJobStep implements JobStep {
	private final JobLogMapper jobLogMapper;
	private final String name;
	private final String message;

	public DbLoggingJobStep(JobLogMapper jobLogMapper, String name, String message) {
		this.jobLogMapper = jobLogMapper;
		this.name = name;
		this.message = message;
	}

	@Override
	public String getName() { return name; }

	@Override
	public void execute(Map<String, Object> context) {
		jobLogMapper.insertLog("chain", name, "OK", message);
	}
}


