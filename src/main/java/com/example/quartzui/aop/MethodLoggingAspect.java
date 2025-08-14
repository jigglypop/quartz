package com.example.quartzui.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MethodLoggingAspect {

	@Pointcut("execution(* com.example.quartzui..*(..)) && !within(com.example.quartzui.aop..*)")
	public void applicationPackagePointcut() {}

	@Around("applicationPackagePointcut()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
		String signature = joinPoint.getSignature().toShortString();
		logger.info("START {}", signature);
		try {
			Object result = joinPoint.proceed();
			long elapsed = System.currentTimeMillis() - start;
			logger.info("END {} ({} ms)", signature, elapsed);
			return result;
		} catch (Throwable t) {
			long elapsed = System.currentTimeMillis() - start;
			logger.error("ERROR {} ({} ms) - {}", signature, elapsed, t.toString());
			throw t;
		}
	}
}


