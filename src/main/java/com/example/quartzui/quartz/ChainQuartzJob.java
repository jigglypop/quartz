package com.example.quartzui.quartz;

import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;

import com.example.quartzui.job.ChainContext;
import com.example.quartzui.job.ChainExecutor;
import com.example.quartzui.job.ChainJobDefinition;
import com.example.quartzui.service.registry.ChainRegistry;

public class ChainQuartzJob implements Job {

	@Override
	public void execute(JobExecutionContext context) {
		String chainName = (String) context.getMergedJobDataMap().get("chainName");
		String errorPolicyBean = (String) context.getMergedJobDataMap().get("errorPolicyBean");
		@SuppressWarnings("unchecked")
		Map<String, Object> input = (Map<String, Object>) context.getMergedJobDataMap().get("input");
		ApplicationContext applicationContext;
		try {
			applicationContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
		} catch (SchedulerException e) {
			throw new RuntimeException("Failed to get ApplicationContext from SchedulerContext", e);
		}
		ChainExecutor executor = applicationContext.getBean(ChainExecutor.class);
		ChainRegistry registry = applicationContext.getBean(ChainRegistry.class);
		ChainJobDefinition def = registry.getChain(chainName);
		try {
			if (input != null) ChainContext.get().putAll(input);
			ChainContext.get().put("errorPolicyBean", errorPolicyBean);
			executor.runChain(def, null, input);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}


