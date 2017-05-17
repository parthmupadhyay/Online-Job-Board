
package com.aop;
/*
import com.dao.PositionRepository;
import com.models.Company;
import com.models.Job_application;
import com.models.Job_seeker;
import com.models.Position;
import com.utility.NotificationService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

*/
/**
 * Created by parth on 5/15/2017.
 *//*

@Aspect
@Component
public class NotificationAspect
{
    @Autowired
    NotificationService notificationService;

    @Autowired
    PositionRepository positionRepository;

    @AfterReturning(pointcut = "execution(* com.controller.PositionController.updatePosition(..))", returning="returnString")
    public void afterPositionUpdate(JoinPoint joinPoint,String returnString)
    {
        System.out.println("In Notification aspect");
        if(returnString.contains("viewjobs"))
        {
            HttpSession session=(HttpSession)joinPoint.getArgs()[1];
            Company company=(Company) session.getAttribute("company");
            Position position=(Position) joinPoint.getArgs()[0];
            StringBuilder message=new StringBuilder("Position has been updated\n");
            message.append("Title:"+position.getTitle()+"\n");
            message.append("Job Description:"+position.getDescription()+"\n");
            message.append("Responsibilities:"+position.getResponsibilities()+"\n");
            message.append("Location:"+position.getLocation()+"\n");
            message.append("Salary:"+position.getSalary()+"\n");
            position=positionRepository.findOne(position.getId());
            notifyJobSeekers(company,position,message.toString());
        }
    }

    @AfterReturning(pointcut = "execution(* com.controller.PositionController.cancelJob(..))", returning="returnString")
    public void afterCancelJob(JoinPoint joinPoint,String returnString)
    {
        if(returnString.contains("position"))
        {
            HttpSession session = (HttpSession) joinPoint.getArgs()[1];
            Company company = (Company) session.getAttribute("company");
            Position position = positionRepository.findOne((Long) joinPoint.getArgs()[0]);
            if(position.getStatus()!=3)
            {
                String message="Thank you for your application.\n" +
                        "\n" +
                        "Unfortunately, we have cancelled the current opening at "+company.getName()+" at this time.  We will keep your resume on file, and will let you know if there's a potential match for a future role.\n" +
                        "\n" +
                        "Thanks again for your interest in "+company.getName()+".  We wish you luck in your search.\n" +
                        "\n" +
                        "Regards,\n" +
                        company.getName()+"  Recruiting";
                notifyJobSeekers(company,position,message);
            }
        }
    }

    private void notifyJobSeekers(Company company, Position position, String message)
    {
        List<Job_seeker> jobSeekers=getNonTerminalJobSeekers(position);
        if(jobSeekers.isEmpty())
            return;
        else
        {
            try
            {
                for(Job_seeker seeker:jobSeekers)
                {
                    notificationService.sendNotificaitoin(seeker, position, company,message);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private List<Job_seeker> getNonTerminalJobSeekers(Position position)
    {
        List<Job_application> applications= position.getJobapplications();
        List<Job_seeker> nonTerminalSeekers=new ArrayList<>();
        for(Job_application application:applications)
        {
            if(application.getStatus()==0||application.getStatus()==0)
                nonTerminalSeekers.add(application.getJob_seeker());

        }
        return nonTerminalSeekers;
    }
}
*/
