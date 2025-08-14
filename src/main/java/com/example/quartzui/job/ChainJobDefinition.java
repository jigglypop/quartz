package com.example.quartzui.job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 여러 JobStep을 순차적으로 실행하는 체인 정의.
 */
public class ChainJobDefinition {
	private final String chainName;
	private final List<JobStep> steps;

	public ChainJobDefinition(String chainName, List<JobStep> steps) {
		this.chainName = chainName;
		this.steps = new ArrayList<>(steps);
	}

	public String getChainName() {
		return chainName;
	}

	public List<JobStep> getSteps() {
		return Collections.unmodifiableList(steps);
	}
}


