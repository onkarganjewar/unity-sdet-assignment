package com.unity3d.project.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.unity3d.project.model.Project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class LoggingAspect {

	/** Logger to log the messages onto console */
	static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

	/** Pointcut expression for createProject */
	@Pointcut("execution(public * com.unity3d.project.controller.ProjectController.createProject(..))")
	public void createProjectPointcut() {
	}

	/** Pointcut expression for hello world*/
	@Pointcut("execution(public * com.unity3d.project.controller.ProjectController.gethelloWorld(..))")
	public void helloWorldPointcut() {
	}

	/** Pointcut expression for requestProject*/
	@Pointcut("execution(public * com.unity3d.project.controller.ProjectController.getProject(..))")
	public void requestProjectPointcut() {
	}

	
	/**
	 * Log before the execution of create project method
	 * @param jp
	 */
	@Before("createProjectPointcut()")
	public void loggingBeforeCreateProject(JoinPoint jp) {
		Object[] projectArgs = jp.getArgs();
		Project p = (Project) projectArgs[0];
		logger.info("ProjectController method:" + jp.getSignature().getName() + " is called with arguments = "+p.toString());
	}

	/**
	 * Log after the project is created successfully
	 * 
	 * @param jp
	 */
	@AfterReturning("createProjectPointcut()")
	public void loggingAfterCreateProject(JoinPoint jp) {
		Object[] projectArgs = jp.getArgs();
		Project p = (Project) projectArgs[0];
		logger.info(jp.getSignature().getName() + " is returned successfully with " + p.toString());
	}

//	@Around("helloWorldPointcut()")
//	public void helloWorldLog(ProceedingJoinPoint joinPoint) throws Throwable {
//		try {
//			logger.info("Before Executing ProjectController method :" + joinPoint.getSignature().getName());
//			joinPoint.proceed();
//		} catch (Exception ex) {
//			logger.error("EXPCETION " + ex.getMessage());
//			throw new Exception(ex);
//		}
//	}

	/**
	 * Log before the execution of request project method
	 * @param jp
	 */
	@Before("requestProjectPointcut()")
	public void loggingBeforeRequestProject(JoinPoint jp) {
		Object[] projectArgs = jp.getArgs();
		StringBuffer sb = new StringBuffer();
		Long projectId = (Long) projectArgs[0];
		if (projectId != null && projectId != 0)
			sb.append("Project ID = "+projectId);
		String country = (String) projectArgs[1];
		if (country!=null && !country.isEmpty())
			sb.append(" && Target country name = "+country);
		String key = (String) projectArgs[2];
		if (key!=null && !key.isEmpty())
			sb.append(" && Keyword = "+key);
		Long num = (Long) projectArgs[3];
		if (num != null && num!= 0)
			sb.append(" && Number = "+num);
		logger.info("ProjectController method:" + jp.getSignature().getName() + " is called with "+sb);
	}

	/**
	 * Log after the successful execution of request project
	 * @param jp
	 */
	@AfterReturning("requestProjectPointcut()")
	public void loggingAfterRequestProject(JoinPoint jp) {
		logger.info(jp.getSignature().getName() + " is returned successfully...");
	}
}
