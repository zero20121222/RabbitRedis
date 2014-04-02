package com.rabbit.test;

import org.springframework.stereotype.Component;

@Component
public class QueueOneListener{
    public void listenOne(Object foo) {
        System.out.println("listen1");
        System.out.println(foo);
    }

    public void listenTwo(Object foo) {
        System.out.println("listen2");
        System.out.println(foo);
    }

    public void listenThree(Object foo) {
        System.out.println("listen3");
        System.out.println(foo);
    }

    public void listenThree1(Object foo){
        System.out.println("listenThree->"+foo);
    }
}