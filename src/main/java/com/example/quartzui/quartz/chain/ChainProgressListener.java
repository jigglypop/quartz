package com.example.quartzui.quartz.chain;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.listeners.JobListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class ChainProgressListener extends JobListenerSupport {

	@Override
	public String getName() { return "chain-progress-listener"; }

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		try {
			JobDataMap dm = context.getMergedJobDataMap();
			String chainRunId = dm.getString("chainRunId");
			String sequence = dm.getString("chainSequence");
			if (sequence == null || chainRunId == null) return; // not a chained job
			int index = dm.getIntValue("chainIndex");
			String[] classes = sequence.split(",");
			int next = index + 1;
			if (jobException != null) {
				String policy = dm.getString("errorPolicyBean");
				if (policy == null) policy = "StopOnFirstErrorPolicy";
				if (policy.equalsIgnoreCase("StopOnFirstErrorPolicy") || policy.equalsIgnoreCase("stop")) {
					return; // stop chain on error
				}
				// else skip-and-continue
			}
			if (next >= classes.length) return; // chain complete

			Class<?> nextClass = Class.forName(classes[next]);
			if (!Job.class.isAssignableFrom(nextClass)) return;

			String name = chainRunId + "-" + (next + 1) + "-" + nextClass.getSimpleName();
			JobDataMap nd = new JobDataMap();
			nd.put("chainRunId", chainRunId);
			nd.put("errorPolicyBean", dm.getString("errorPolicyBean"));
			nd.put("chainSequence", sequence);
			nd.put("chainIndex", next);

			JobKey jobKey = new JobKey(name, "chain");
			Scheduler scheduler = context.getScheduler();
			// Ensure JobDetail exists (create if missing)
			JobDetail jd = scheduler.getJobDetail(jobKey);
			if (jd == null) {
				@SuppressWarnings("unchecked")
				Class<? extends Job> clazz = (Class<? extends Job>) nextClass;
				jd = JobBuilder.newJob(clazz)
						.withIdentity(jobKey)
						.storeDurably()
						.build();
				scheduler.addJob(jd, true);
			}

			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(name + "-trigger", "chain")
					.forJob(jobKey)
					.usingJobData(nd)
					.startNow()
					.build();
			scheduler.scheduleJob(trigger);
		} catch (ClassNotFoundException | SchedulerException e) {
			getLog().error("Failed to schedule next step in chain", e);
		}
	}
}


