package com.annotation.test;

import com.annotation.service.AnnotationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;

/**
 * Annotation的业务逻辑通过java反射机制注入
 * Created by zero on 14-4-6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/root-context.xml"
})
public class FrameTest {
    @Autowired
    private AnnotationService annotationService;

    @Test
    public void testAopAnnotation(){
        annotationService.test();
    }

    @Test
    public void testAnnotation(){
        try{
            Class annotation = Class.forName("com.annotation.test.AnnotationTest");
            Method[] methods = annotation.getMethods();

            for(Method method : methods){
                //判断是否有指定的注解类型（这种方式相当于是一种装饰者模式）
                if(method.isAnnotationPresent(MyAnnotation.class)){
                    //根据注解类型返回制定的类型的注解
                    //Description description = (Description)annotation.getAnnotation(MyAnnotation.class);
                    MyAnnotation myAnnotation = method.getAnnotation(MyAnnotation.class);

                    System.out.println("lockName:"+myAnnotation.lockName()+"\n maxWait:"+myAnnotation.maxWait()+"\n expiredTime:"+myAnnotation.expiredTime());
                    //创建一个实体类对象,执行方法体
                    method.invoke(annotation.newInstance());
                    System.out.println("end");
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
}
