package com.annotation.test;

/**
 * Annotation的测试类
 * Created by zero on 14-4-6.
 */
public class AnnotationTest {
    @MyAnnotation(lockName = "TestLock" , maxWait = 1000, expiredTime = 10)
    public void test(){
        System.out.println("This is a annotation test class!");
    }
}
