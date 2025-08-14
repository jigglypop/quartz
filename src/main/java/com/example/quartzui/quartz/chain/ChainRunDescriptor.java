package com.example.quartzui.quartz.chain;

import java.util.List;

import org.quartz.JobKey;

public class ChainRunDescriptor {
	private final String chainRunId;
	private final List<JobKey> jobKeys;
	private final String policy; // StopOnFirstErrorPolicy | SkipAndCollectErrorPolicy | alias

	public ChainRunDescriptor(String chainRunId, List<JobKey> jobKeys, String policy) {
		this.chainRunId = chainRunId;
		this.jobKeys = jobKeys;
		this.policy = policy;
	}

	public String getChainRunId() { return chainRunId; }
	public List<JobKey> getJobKeys() { return jobKeys; }
	public String getPolicy() { return policy; }
}


