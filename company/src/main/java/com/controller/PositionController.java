package com.controller;

import com.dao.CompanyRepository;
import com.dao.PositionRepository;
import com.models.Company;
import com.models.Job_application;
import com.models.Job_seeker;
import com.models.Position;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.utility.NotificationService;
import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by parth on 5/13/2017.
 */
@Controller
public class PositionController
{
    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    NotificationService notificationService;

    @RequestMapping(value = "/postjob",method = RequestMethod.GET)
    public String loadPostJob(Model model)
    {
        Position position= new Position();
        model.addAttribute("position", position);

        return "postjob";
    }

    @RequestMapping(value = "/postjob",method = RequestMethod.POST)
    public String addNewPosition(@ModelAttribute("position") Position position,
                                 HttpSession session)
    {
        System.out.print("in post job: "+position.getLocation());
        Company company = (Company)session.getAttribute("company");
        position.setCompany(company);
        positionRepository.save(position);
        return "redirect:/viewjobs";
    }

    @RequestMapping(value = "/updatePosition/{id}",method = RequestMethod.GET)
    public String loadUpdatePosition(@PathVariable Long id,
                                     Model model)
    {
        Position position=positionRepository.findOne(id);
        model.addAttribute("position",position);
        return "updatePosting";
    }

    @RequestMapping(value = "/updatePosition",method = RequestMethod.POST)
    public String updatePosition(@ModelAttribute("position") Position position, HttpSession session, HttpServletRequest request)
    {
        if(session.getAttribute("company")!=null)
        {
            Company company=(Company) session.getAttribute("company");
            Position updatePosition = positionRepository.findOne(position.getId());
            if(checkIfPositionBelongsToCompany(updatePosition,company))
            {
                updatePosition.setDescription(position.getDescription());
                updatePosition.setLocation(position.getLocation());
                updatePosition.setResponsibilities(position.getResponsibilities());
                updatePosition.setSalary(position.getSalary());
                updatePosition.setTitle(position.getTitle());
                positionRepository.save(updatePosition);
                StringBuilder message=new StringBuilder("Position has been updated\n");
                message.append("Title:"+updatePosition.getTitle()+"\n");
                message.append("Job Description:"+updatePosition.getDescription()+"\n");
                message.append("Responsibilities:"+updatePosition.getResponsibilities()+"\n");
                message.append("Location:"+updatePosition.getLocation()+"\n");
                message.append("Salary:"+updatePosition.getSalary()+"\n");
                notifyJobSeekers(company,updatePosition,message.toString());
                return "redirect:/viewjobs";
            }
            else
                return "errorpage";

        }
        else
            return "login";
    }

    @RequestMapping(value = "/viewjobs",method = RequestMethod.GET)
    public String viewPositions(Model model, HttpSession session)
    {
        if(session.getAttribute("company")!=null) {
            Company company_id = (Company) session.getAttribute("company");
            Company company = companyRepository.findOne((long) company_id.getId());//get company id from session
            List<Position> positionList = company.getPositions();
            System.out.println(positionList.toString());
            model.addAttribute("positionList", positionList);
            ArrayList<Boolean> filters=new ArrayList<>();
            filters.add(false);
            filters.add(false);
            filters.add(false);
            model.addAttribute("filters",filters);
            return "viewjobs";
        }
        else
            return "login";
    }

    @RequestMapping(value = "/viewjobs",method = RequestMethod.POST)
    public String filterViewPositions(HttpSession session,
                                      Model model,
                                      @RequestParam(required = false) Boolean open,
                                      @RequestParam(required = false) Boolean cancelled,
                                      @RequestParam(required = false) Boolean filled)
    {
        if(session.getAttribute("company")!=null)
        {
            Company company_id = (Company) session.getAttribute("company");
            Company company = companyRepository.findOne((long) company_id.getId());
            List<Position> positionList=new ArrayList<Position>();
            List<Boolean> filters=new ArrayList<>();
            if(open==null && cancelled==null && filled==null)
            {
                positionList=company.getPositions();
                filters.add(false);
                filters.add(false);
                filters.add(false);
                model.addAttribute("filters",filters);
                model.addAttribute("positionList",positionList);

                return "viewjobs";
            }
            if(open!=null)
            {
                filters.add(true);
                positionList.addAll(positionRepository.findByStatusAndCompany(0,company));
            }
            else
                filters.add(false);

            if(cancelled!=null)
            {
                filters.add(true);
                positionList.addAll(positionRepository.findByStatusAndCompany(2,company));
            }
            else
                filters.add(false);
            if(filled!=null)
            {
                filters.add(true);
                positionList.addAll(positionRepository.findByStatusAndCompany(1,company));
            }
            else
                filters.add(false);
            model.addAttribute("filters",filters);
            model.addAttribute("positionList",positionList);
            return "viewjobs";
        }
        else
        {
            return "login";
        }
    }


    @RequestMapping(value = "/position/{id}", method = RequestMethod.GET)
    public String viewJob(@PathVariable long id,Model model,HttpSession session)
    {
        if(session.getAttribute("company")!=null)
        {
            Company company=(Company) session.getAttribute("company");
            List<Position> positionList=company.getPositions();
            Position position = positionRepository.findOne(id);

            if(checkIfPositionBelongsToCompany(position,company))
            {
                model.addAttribute("position", position);
                return "job";
            }
            else
            {
                return "errorpage";
            }
        }
        else
            return "login";
    }


    @RequestMapping(value="/position/cancel",method = RequestMethod.POST)
    public String cancelJob(@RequestParam long id,HttpSession session)
    {
        if(session.getAttribute("company")!=null)
        {
            Company company=(Company) session.getAttribute("company");
            Position position = positionRepository.findOne(id);

            if(checkIfPositionBelongsToCompany(position,company))
            {
                List<Job_application> applications= position.getJobapplications();
                List<Job_seeker> nonTerminalSeekers=new ArrayList<>();
                for (Job_application application:applications)
                {
                    if(application.getStatus()==3)
                    {
                        return "redirect:/position/"+id;
                    }
                    if(application.getStatus()==0||application.getStatus()==0)
                        nonTerminalSeekers.add(application.getJob_seeker());

                }
                position.setStatus(2);
                positionRepository.save(position);
                //notify non terminal applicants --should be moved to AOP
                String message="Thank you for your application.\n" +
                                "\n" +
                                "Unfortunately, we have cancelled the current opening at "+company.getName()+" at this time.  We will keep your resume on file, and will let you know if there's a potential match for a future role.\n" +
                                "\n" +
                                "Thanks again for your interest in "+company.getName()+".  We wish you luck in your search.\n" +
                                "\n" +
                                "Regards,\n" +
                                company.getName()+"  Recruiting";
                notifyJobSeekers(company,position,message);
                return "redirect:/position/"+id;
            }
            return "errorpage";
        }
        else
            return "login";
    }

    private void notifyJobSeekers(Company company,Position position,String message)
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

    private boolean checkIfPositionBelongsToCompany(Position position,Company company)
    {
        boolean flag=false;
        List<Position> positionList=company.getPositions();
        for (Position temp:positionList)
        {
            if(temp.getId()==position.getId())
                flag=true;
        }
        return flag;
    }
}