package com.aop;

import com.controller.PositionController;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpSession;

/**
 * Created by parth on 5/15/2017.
 */
@Aspect
@Component
public class SecurityAspect
{
    @Pointcut("within(@org.springframework.stereotype.Controller *) && " +
                    "@annotation(requestMapping) && " +
                    "execution(* com.controller.PositionController.*(..))"
    )
    public void controller(RequestMapping requestMapping) {}

    @Around("controller(requestMapping)")
    public Object beforeAll(ProceedingJoinPoint joinPoint, RequestMapping requestMapping) throws Throwable
    {
        System.out.println(joinPoint.getSignature());
        Object[] args=joinPoint.getArgs();
        HttpSession session=(HttpSession) args[args.length-1];
        if(session.getAttribute("company")==null)
        {
            System.out.println("Redirecting to login");
            return "/login";
        }
        return joinPoint.proceed();
    }
}
