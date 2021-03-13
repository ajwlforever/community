package com.ajwlforever.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AlphaAspect {

    @Pointcut("execution(* com.ajwlforever.community.service.*.*(..))")
    public  void pointCut()
    {
    }


    @Before("pointCut()")
    public void before()
    {
        System.out.println("Before");
    }

    @After("pointCut()")
    public void after()
    {
        System.out.println("After");
    }


    @AfterReturning("pointCut()")
    public void afterReturning()
    {
        System.out.println("AfterReturning");
    }


    @AfterThrowing("pointCut()")
    public void afterThrowing()
    {
        System.out.println("AfterThrowing");
    }

    @Around("pointCut()")
    public Object aroud(ProceedingJoinPoint joinPoint) throws  Throwable
    {
        System.out.println("Around Before");
        Object obj =
        joinPoint.proceed(); // 调目标组件的方法

        System.out.println("Around After");
        return obj;
    }


}
