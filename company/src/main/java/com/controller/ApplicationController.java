package com.controller;

import com.dao.ApplicationRepository;
import com.dao.PositionRepository;
import com.models.Job_application;
import com.models.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by parth on 5/16/2017.
 */
@Controller
public class ApplicationController
{
    @Autowired
    PositionRepository positionRepository;

    @Autowired
    ApplicationRepository applicationRepository;

    @RequestMapping(value = "/applications/{id}",method = RequestMethod.GET)
    public String getApplications(@PathVariable Long id, Model model, HttpSession session)
    {
        Position position=positionRepository.findOne(id);
        List<Job_application> applications=position.getJobapplications();
        model.addAttribute("applications",applications);
        return "applications";
    }

    @RequestMapping(value = "/jobapplication/{id}",method = RequestMethod.GET)
    public String getApplication(@PathVariable int id, Model model, HttpSession session)
    {
        Job_application jobapplication=applicationRepository.findOne(id);
        model.addAttribute("jobapplication",jobapplication);
        return "jobapplication";
    }

    @RequestMapping(value = "/application/reject/{id}",method = RequestMethod.GET)
    public String rejectApplication(@PathVariable int id, HttpSession session)
    {
        Job_application application=applicationRepository.findOne(id);
        application.setStatus(2);
        applicationRepository.save(application);
        return "redirect:/jobapplication/"+id;
    }

    @RequestMapping(value = "/application/offer/{id}",method = RequestMethod.GET)
    public String makeOffer(@PathVariable int id, HttpSession session)
    {
        Job_application application=applicationRepository.findOne(id);
        application.setStatus(1);
        applicationRepository.save(application);
        return "redirect:/jobapplication/"+id;
    }
}
