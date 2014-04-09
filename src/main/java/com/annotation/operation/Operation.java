package com.annotation.operation;

import com.annotation.test.MyAnnotation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.Before;
import org.springframework.stereotype.Component;

/**
 * 这个是Spring提供的针对于Annotation的处理器
 * Created by zero on 14-4-7.
 */
@Aspect
@Component
public class Operation {
    //execution (* *(..))表示匹配所有方法
    @Around("execution (* *(..)) && @annotation(myAnnotation)")
    public void aroundAnnotation(ProceedingJoinPoint pjp , MyAnnotation myAnnotation){
        System.out.println("AnnotationService Operation start");
        System.out.println("pjp->"+pjp);
        System.out.println("myAnnotation->"+myAnnotation);
        System.out.println("lockName->"+myAnnotation.lockName());
        System.out.println("maxWait->"+myAnnotation.maxWait());
        System.out.println("expiredTime->"+myAnnotation.expiredTime());

        try {
            //执行原有的程序方法（即service的test方法）
            pjp.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
