package com.ankur.keepurl.app.logging;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class KeepURLLoggerInterceptor {
	
	@Pointcut("execution (* com.ankur.keepurl.manager.api.*.*(..))")
	public void servicePackage() {}
	
	@Pointcut("execution (* com.ankur.keepurl.security.api.impl.*.*(..))")
	public void securityPackage() {}
	
	@Around("servicePackage() || securityPackage()")
	public Object aroundService(ProceedingJoinPoint joinPoint) throws Throwable {
		
		final Logger LOGGER = LoggerFactory
							.getLogger(joinPoint.getTarget().getClass());
		final String methodName = ((MethodSignature) joinPoint
								.getSignature()).getMethod().getName();;		
		StringBuilder args = new StringBuilder();
		for (Object arg : joinPoint.getArgs()) {
			 args.append(", ").append(arg.toString());
		}		
		LOGGER.info("Entering Method [{}{}]", methodName, args.toString());		
		try {		
			Object returnValue = joinPoint.proceed();	
			if (returnValue instanceof List<?>) {
				LOGGER.info("Exiting Method [{}, {} Objects]", methodName, ((List<?>)returnValue).size());
			} else {
				LOGGER.info("Exiting Method [{}, {}]", methodName, returnValue);
			}		
			return returnValue;		
		} catch (Throwable exception) {			
			LOGGER.error("Exception on Method {}: {}", methodName, exception.getMessage());
			throw exception;
		}		
	}
}