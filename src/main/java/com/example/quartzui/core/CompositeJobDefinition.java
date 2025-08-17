package com.example.quartzui.core;

import java.util.List;

public class CompositeJobDefinition {
	private final String jobName;
	private final List<JobAtom> atoms;

	public CompositeJobDefinition(String jobName, List<JobAtom> atoms) {
		this.jobName = jobName;
		this.atoms = atoms;
	}

	public String getJobName() { return jobName; }
	public List<JobAtom> getAtoms() { return atoms; }
}


