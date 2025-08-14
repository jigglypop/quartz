package com.example.quartzui.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.quartzui.job.error.JobErrorPolicy;

/**
 * 체인 실행 엔진: 관심사 분리(정의/실행/에러정책) 구현.
 */
@Component
public class ChainExecutor {

	private static final Logger log = LoggerFactory.getLogger(ChainExecutor.class);

 	public void runChain(ChainJobDefinition chainDefinition, JobErrorPolicy errorPolicy, Map<String, Object> inputContext) throws Exception {
		Map<String, Object> context = new HashMap<>();
		if (inputContext != null) context.putAll(inputContext);
		ChainContext.get().putAll(context);

		List<JobStep> steps = chainDefinition.getSteps();
		for (int i = 0; i < steps.size(); i++) {
			JobStep step = steps.get(i);
			log.info("[CHAIN:{}] STEP {} - {} START", chainDefinition.getChainName(), i + 1, step.getName());
			step.execute(ChainContext.get());
			log.info("[CHAIN:{}] STEP {} - {} END", chainDefinition.getChainName(), i + 1, step.getName());
		}
	}
}


