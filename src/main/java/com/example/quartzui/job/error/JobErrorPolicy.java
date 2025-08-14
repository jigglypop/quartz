package com.example.quartzui.job.error;

import java.util.Map;

import com.example.quartzui.job.JobStep;

/**
 * 체인 실행 중 에러 처리 전략의 SPI.
 */
public interface JobErrorPolicy {
	/**
	 * @return true 이면 체인 중단, false 이면 계속 진행
	 */
	boolean handle(JobStep step, int stepIndex, Exception exception, Map<String, Object> context);
}


