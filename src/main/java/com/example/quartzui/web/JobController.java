package com.example.quartzui.web;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.quartzui.quartz.ChainQuartzJob;

@Controller
public class JobController {

	private final Scheduler scheduler;

	public JobController(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@GetMapping("/")
	public String index(Model model) throws SchedulerException {
		model.addAttribute("jobs", scheduler.getJobGroupNames());

		java.util.List<java.util.Map<String, Object>> jobDetails = new java.util.ArrayList<>();
		for (String group : scheduler.getJobGroupNames()) {
			for (org.quartz.JobKey jk : scheduler.getJobKeys(org.quartz.impl.matchers.GroupMatcher.jobGroupEquals(group))) {
				java.util.Map<String, Object> row = new java.util.HashMap<>();
				row.put("group", group);
				row.put("name", jk.getName());
				java.util.List<java.util.Map<String, Object>> triggers = new java.util.ArrayList<>();
				for (org.quartz.Trigger t : scheduler.getTriggersOfJob(jk)) {
					java.util.Map<String, Object> tr = new java.util.HashMap<>();
					tr.put("key", t.getKey().toString());
					tr.put("state", scheduler.getTriggerState(t.getKey()).name());
					java.util.Date next = t.getNextFireTime();
					java.util.Date prev = t.getPreviousFireTime();
					tr.put("nextFireTime", next != null ? next.toString() : "-");
					tr.put("prevFireTime", prev != null ? prev.toString() : "-");
					triggers.add(tr);
				}
				row.put("triggers", triggers);
				jobDetails.add(row);
			}
		}
		model.addAttribute("jobDetails", jobDetails);
		return "index";
	}

	@PostMapping("/trigger")
	public String trigger(@RequestParam(defaultValue = "StopOnFirstErrorPolicy") String policy,
	                     @RequestParam(defaultValue = "demo") String chainName) throws SchedulerException {
		Map<String, Object> input = new HashMap<>();

		JobDataMap data = new JobDataMap();
		data.put("chainName", chainName);
		data.put("errorPolicyBean", policy);
		data.put("input", input);

		JobDetail jobDetail = JobBuilder.newJob(ChainQuartzJob.class)
				.withIdentity("chainJob", "default")
				.usingJobData(data)
				.storeDurably()
				.build();


		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity("chainTrigger", "default")
				.startNow()
				.forJob(jobDetail)
				.build();

		if (scheduler.checkExists(jobDetail.getKey())) {
			scheduler.deleteJob(jobDetail.getKey());
		}
		scheduler.scheduleJob(jobDetail, java.util.Set.of(trigger), true);

		return "redirect:/";
	}
}


