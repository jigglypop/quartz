package com.example.quartzui.job.error;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class JobErrorPolicyRegistry {

	private final Map<String, JobErrorPolicy> idToPolicy = new HashMap<>();

	public JobErrorPolicyRegistry(List<JobErrorPolicy> policies) {
		for (JobErrorPolicy p : policies) {
			String simple = p.getClass().getSimpleName();
			idToPolicy.put(simple, p);
			idToPolicy.put(simple.toLowerCase(), p);
		}
		// common aliases
		registerAlias("stop", "StopOnFirstErrorPolicy");
		registerAlias("skip", "SkipAndCollectErrorPolicy");
	}

	private void registerAlias(String alias, String targetSimpleClassName) {
		JobErrorPolicy target = idToPolicy.get(targetSimpleClassName);
		if (target != null) {
			idToPolicy.put(alias, target);
		}
	}

	public JobErrorPolicy get(String id) {
		String key = id;
		if (key == null) key = "";
		JobErrorPolicy policy = idToPolicy.get(key);
		if (policy == null) policy = idToPolicy.get(key.toLowerCase());
		if (policy == null) throw new IllegalArgumentException("Unknown JobErrorPolicy: " + id);
		return policy;
	}
}


