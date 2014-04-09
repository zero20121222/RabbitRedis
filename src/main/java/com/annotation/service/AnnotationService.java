package com.annotation.service;

import com.annotation.test.MyAnnotation;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 这个是Spring Aop自定义注解的测试类
 * Created by zero on 14-4-7.
 */
@Service
@Component
public class AnnotationService {

    @MyAnnotation(lockName = "TestLock" , maxWait = 1000, expiredTime = 10)
    public void test(){
        System.out.println("This is a annotation test class!");
    }
}
