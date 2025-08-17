package com.example.quartzui.quartz;

import java.util.HashMap;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.transaction.annotation.Transactional;

import com.example.quartzui.core.CompositeJobDefinition;
import com.example.quartzui.core.JobAtom;

public class CompositeQuartzJob implements Job {

	@Override
	@Transactional
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Object defObj = context.getMergedJobDataMap().get("definition");
		if (!(defObj instanceof CompositeJobDefinition)) {
			throw new JobExecutionException("CompositeJobDefinition is required in JobDataMap under key 'definition'");
		}
		CompositeJobDefinition def = (CompositeJobDefinition) defObj;
		Map<String, Object> shared = new HashMap<>();
		for (JobAtom atom : def.getAtoms()) {
			try {
				atom.execute(shared);
			} catch (Exception e) {
				throw new JobExecutionException("Atom failed: " + atom.name(), e);
			}
		}
	}
}


