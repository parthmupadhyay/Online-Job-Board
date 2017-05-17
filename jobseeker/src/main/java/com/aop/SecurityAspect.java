package com.aop;

import com.controller.JobApplicationController;
import com.models.Job_seeker;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
                    "execution(* com.controller.*.*(..))"
    )
    public void controller(RequestMapping requestMapping) {}

    private static final Logger log = LoggerFactory.getLogger(SecurityAspect.class);


    @Around("controller(requestMapping)")
    public Object beforeAll(ProceedingJoinPoint joinPoint, RequestMapping requestMapping) throws Throwable
    {
        log.debug("---------------inside security aop");
        System.out.println(joinPoint.getSignature());
        Object[] args=joinPoint.getArgs();
        System.out.println(args.length);
        HttpSession session=(HttpSession) args[1]; //2nd arng is always session
        Job_seeker jobseeker =(Job_seeker) session.getAttribute("jobseeker");
        if(jobseeker==null)
        {
           log.debug("Redirecting to login");
            return "/login";
        }else if(jobseeker.getIsActivated() == 0 ){
            log.debug("Not activated user hence Redirecting to login");
            return "/login";
        }
        return joinPoint.proceed();
    }
}
