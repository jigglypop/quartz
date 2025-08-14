package com.example.quartzui.service.registry;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.quartzui.job.ChainJobDefinition;
import com.example.quartzui.service.DemoChainFactory;

@Component
public class ChainRegistry {
	private final Map<String, ChainJobDefinition> registry = new HashMap<>();

	public ChainRegistry(DemoChainFactory demoChainFactory) {
		registry.put("demo", demoChainFactory.sampleChainSimple());
		registry.put("demo-db", demoChainFactory.sampleChainWithDb());
	}

	public ChainJobDefinition getChain(String name) {
		ChainJobDefinition def = registry.get(name);
		if (def == null) throw new IllegalArgumentException("Unknown chain: " + name);
		return def;
	}
}


