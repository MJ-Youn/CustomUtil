package kr.co.lguplus.nw.cert.mgmt.api.aop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import kr.co.lguplus.nw.cert.mgmt.api.util.DataUtil;

@Component
@Aspect
public class AOPLogger {

	private Logger logger = LoggerFactory.getLogger(getClass());
	public static final int SIGNATURE_STRING_LENGTH = -45;
	
	private static final String PACKAGE_NAME = "kr.co.lguplus.nw.cert.mgmt.api";
	
	@Before("execution(* " + AOPLogger.PACKAGE_NAME + ".controller.*.*(..)) || execution(* " + AOPLogger.PACKAGE_NAME + ".repository.*.*(..))")
	public void logControlloerNRepositoryArgs(JoinPoint joinPoint) {
		StringBuffer logStringBuffer = new StringBuffer();
		Object[] args = joinPoint.getArgs();
		
		logStringBuffer.append("[")
						.append(AOPLogger.fixLength(joinPoint.getSignature().toShortString()))
						.append("] ");
		
		if (args.length == 0) {
			logStringBuffer.append("No have arguments.");
		} else {
			logStringBuffer.append("a r g s  >> ");
			IntStream.range(0, args.length)
						.forEach(idx -> {
							logStringBuffer.append(DataUtil.toString(args[idx]));
							
							if (idx != args.length-1) {
								logStringBuffer.append(", ");
							}
						});
		}
		
		logger.info("{}", logStringBuffer.toString());
	}
	
	@AfterReturning(pointcut = "execution(* " + AOPLogger.PACKAGE_NAME + ".controller.*.*(..))", returning = "output")
	public void logControllerReturns(JoinPoint joinPoint, ResponseEntity<Object> output) {
		logger.info("[{}] Response >> {}, {}", AOPLogger.fixLength(joinPoint.getSignature().toShortString()), output.getStatusCode(), DataUtil.toString(output.getBody()));
	}
	
	@Before("execution(* " + AOPLogger.PACKAGE_NAME + ".service.impl.*.*(..))")
	public void logServiceEnter(JoinPoint joinPoint) {
		logger.info("[{}] Called", AOPLogger.fixLength(joinPoint.getSignature().toShortString()));
	}
	
	@AfterReturning(pointcut = "execution(* " + AOPLogger.PACKAGE_NAME + ".service.impl.*.*(..)) || execution(* " + AOPLogger.PACKAGE_NAME + ".repository.*.*(..))", returning = "output")
	public void logServiceReturns(JoinPoint joinPoint, Object output) {
		String shortName = AOPLogger.fixLength(joinPoint.getSignature().toShortString());
		Class<?> outputClass = ((MethodSignature)joinPoint.getSignature()).getReturnType();
		StringBuffer logStringBuffer = new StringBuffer();
		
		if (outputClass == void.class) {
			logger.info("[{}] success", shortName);
		} else {
			if (outputClass == List.class || outputClass == ArrayList.class) {
				logStringBuffer.append("List{");
				
				for (int i = 0 ; i < ((List<?>)output).size() ; i++) {
					logStringBuffer.append(DataUtil.toString(((List<?>)output).get(i)))
									.append(i == ((List<?>)output).size()-1 ? "" : ", ");
				}
				
				logStringBuffer.append("}");
			} else if (outputClass == String[].class) {
				logStringBuffer.append("Array{");
				
				for (int i = 0 ; i < ((String[])output).length ; i++) {
					logStringBuffer.append(((String[])output)[i].toString())
									.append(i == ((String[])output).length-1 ? "" : ", ");
				}
				
				logStringBuffer.append("}");
			} else if (output == null) {
				logStringBuffer.append("null");
			} else {
				logStringBuffer.append(DataUtil.toString(output));
			}
			
			logger.info("[{}] returns  >> {}", shortName, logStringBuffer.toString());
		}
	}
	
	public static String fixLength(String str) {
		StringBuffer stringFormat = new StringBuffer().append("%").append(SIGNATURE_STRING_LENGTH).append("s");
		return String.format(stringFormat.toString(), str);
	}
	
}
