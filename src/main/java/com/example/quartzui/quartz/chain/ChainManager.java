package com.example.quartzui.quartz.chain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Component;

@Component
public class ChainManager {

	private final Scheduler scheduler;

	public ChainManager(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public ChainRunDescriptor startSequential(String policy, List<Class<? extends org.quartz.Job>> jobs) throws SchedulerException {
		String chainRunId = UUID.randomUUID().toString();
		List<JobKey> keys = new ArrayList<>();

		if (jobs.isEmpty()) return new ChainRunDescriptor(chainRunId, keys, policy);

		// Prepare chain sequence as comma-separated FQCNs for listener-driven chaining
		StringBuilder seq = new StringBuilder();
		for (int i = 0; i < jobs.size(); i++) {
			if (i > 0) seq.append(',');
			seq.append(jobs.get(i).getName());
		}

		Class<? extends org.quartz.Job> first = jobs.get(0);
		String name = chainRunId + "-1-" + first.getSimpleName();
		JobDataMap data = new JobDataMap();
		data.put("chainRunId", chainRunId);
		data.put("errorPolicyBean", policy);
		data.put("chainSequence", seq.toString());
		data.put("chainIndex", 0); // zero-based index

		JobDetail jd = JobBuilder.newJob(first)
				.withIdentity(name, "chain")
				.usingJobData(data)
				.storeDurably()
				.build();
		Trigger t = TriggerBuilder.newTrigger()
				.withIdentity(name + "-trigger", "chain")
				.startNow()
				.forJob(jd)
				.build();
		scheduler.addJob(jd, true);
		scheduler.scheduleJob(t);
		keys.add(jd.getKey());

		return new ChainRunDescriptor(chainRunId, keys, policy);
	}
}


