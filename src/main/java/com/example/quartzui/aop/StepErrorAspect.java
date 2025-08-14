package com.example.quartzui.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.example.quartzui.job.ChainContext;
import com.example.quartzui.job.JobStep;
import com.example.quartzui.job.error.JobErrorPolicy;
import com.example.quartzui.job.error.JobErrorPolicyRegistry;

@Aspect
@Component
public class StepErrorAspect {

	private final JobErrorPolicyRegistry policyRegistry;

	public StepErrorAspect(JobErrorPolicyRegistry policyRegistry) {
		this.policyRegistry = policyRegistry;
	}

	@Pointcut("execution(* com.example.quartzui.job.JobStep+.execute(..))")
	public void anyJobStepExecute() {}

	@Around("anyJobStepExecute()")
	public Object aroundStep(ProceedingJoinPoint pjp) throws Throwable {
		try {
			return pjp.proceed();
		} catch (Throwable ex) {
			Object target = pjp.getTarget();
			String policyId = (String) ChainContext.get().getOrDefault("errorPolicyBean", "StopOnFirstErrorPolicy");
			JobErrorPolicy policy = policyRegistry.get(policyId);
			boolean stop = policy.handle((JobStep) target,
					Integer.parseInt(String.valueOf(ChainContext.get().getOrDefault("stepIndex", -1))),
					(ex instanceof Exception) ? (Exception) ex : new RuntimeException(ex),
					ChainContext.get());
			if (stop) throw ex;
			return null;
		}
	}
}


