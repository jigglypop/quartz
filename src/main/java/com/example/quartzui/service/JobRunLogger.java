package com.example.quartzui.service;

import org.springframework.stereotype.Component;

import com.example.quartzui.job.ChainContext;
import com.example.quartzui.mapper.JobLogMapper;

@Component
public class JobRunLogger {

	private final JobLogMapper jobLogMapper;

	public JobRunLogger(JobLogMapper jobLogMapper) {
		this.jobLogMapper = jobLogMapper;
	}

	public void start(String stepName) {
		jobLogMapper.insertLog(currentJob(), stepName, "START", "");
	}

	public void success(String stepName) {
		jobLogMapper.insertLog(currentJob(), stepName, "SUCCESS", "");
	}

	public void error(String stepName, String message) {
		jobLogMapper.insertLog(currentJob(), stepName, "ERROR", message);
	}

	private String currentJob() {
		Object rid = ChainContext.get().get("chainRunId");
		return rid != null ? String.valueOf(rid) : "chain";
	}
}


