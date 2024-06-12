package com.objects.marketbridge.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Aspect
@Slf4j
@Profile({"local", "dev"})
@Component
public class LogAspect {

    // 반환타입 상관X, com.objects.marketbridge 하위 모든 패키지, 메서드이름 상관X, 파라미터 상관 X
    @Pointcut("execution(" +
            "* " +
            "com.objects.marketbridge.." +
            "*.*" +
            "(..))")
    public void all() {
    }

    // 반환타입 상관X, com.objects.marketbridge 하위 모든 패키지, *Controller로 끝나는 메서드이름, 파라미터 상관 X
//    @Pointcut("execution(* com.objects.marketbridge..*Controller.*(..))")
    @Pointcut("execution(" +
            "* " +
            "com.objects.marketbridge..*.controller.." +
            "*.*" +
            "(..))")
    public void controller() {
    }

    // 반환타입 상관X, com.objects.marketbridge 하위 모든 패키지, *Service 끝나는 메서드이름, 파라미터 상관 X
//    @Pointcut("execution(* com.objects.marketbridge..*Service.*(..))")
    @Pointcut("execution(" +
            "* " +
            "com.objects.marketbridge..*.service.." +
            "*.*" +
            "(..))")
    public void service(){}

    // 반환타입 상관X, com.objects.marketbridge 하위 모든 패키지, *RepositoryImpl 끝나는 메서드이름, 파라미터 상관 X
//    @Pointcut("execution(* com.objects.marketbridge..*RepositoryImpl.*(..))")
    @Pointcut("execution(* com.objects.marketbridge..*.infra..*.*(..))")
    public void repository(){}

    @Pointcut("execution(* com.objects.marketbridge..*.domain..*.*(..))")
    public void domain() {}

    @Around("controller() || service() || repository() || domain()")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            log.info(formatter("[CLASS] {}"), methodSignature.getDeclaringType().getSimpleName());
            log.info(formatter("[METHOD] {}"),  methodSignature.getMethod().getName());

            Object[] args = joinPoint.getArgs();

            // 출력 -> [PARAMETER_1] 클래스타입, 변수이름, 값
            IntStream.range(0, args.length)
                    .filter(i -> args[i] != null)
                    .forEach(i -> log.info(formatter("[PARAMETER_{}] {} {} = {}"),
                            i,
                            methodSignature.getParameterTypes()[i].getSimpleName(),
                            methodSignature.getParameterNames()[i],
                            args[i]));
            log.info(formatter("[RETURN] {}"),methodSignature.getReturnType().getSimpleName());
            log.info(formatter("[TIMER] {}ms"), timeMs);
            log.info("");
        }
    }

    // prefix가 무조건 13칸의 공간을 확보하게 해주는 메서드
    public static String formatter(String msg) {
        StringBuilder sb = new StringBuilder();
        String prefix = String.valueOf(sb.append(msg.split("]")[0]).append("]"));
        sb.setLength(0);
        String body = msg.split("]")[1];
        String formatted = String.format("%-13s", prefix);

        return String.valueOf(sb.append(formatted).append(" ").append(body));
    }
}
