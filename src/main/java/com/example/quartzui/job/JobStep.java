package com.example.quartzui.job;

import java.util.Map;

/**
 * 단일 Job 단계를 표현. 체이닝 가능한 최소 단위.
 */
public interface JobStep {
	String getName();
	void execute(Map<String, Object> context) throws Exception;
}


