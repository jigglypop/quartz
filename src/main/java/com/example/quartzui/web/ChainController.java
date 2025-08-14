package com.example.quartzui.web;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.quartzui.quartz.ChainQuartzJob;

@Controller
public class ChainController {

	private final Scheduler scheduler;

	public ChainController(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@PostMapping("/chain/run")
	public String runChain(@RequestParam(defaultValue = "StopOnFirstErrorPolicy") String policy,
	                      @RequestParam(defaultValue = "demo") String chainName) throws SchedulerException {
		String runId = UUID.randomUUID().toString();
		Map<String, Object> input = new HashMap<>();

		JobDataMap data = new JobDataMap();
		data.put("chainName", chainName);
		data.put("errorPolicyBean", policy);
		data.put("input", input);
		data.put("chainRunId", runId);

		JobDetail jobDetail = JobBuilder.newJob(ChainQuartzJob.class)
				.withIdentity("composite-" + runId, "chain")
				.usingJobData(data)
				.build();

		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity("composite-" + runId + "-trigger", "chain")
				.forJob(jobDetail)
				.startNow()
				.build();

		scheduler.scheduleJob(jobDetail, trigger);
		return "redirect:/?run=" + runId;
	}
}


