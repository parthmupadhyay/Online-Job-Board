package com.controller;

import com.dao.JobSeekerRepository;
import com.models.Job_seeker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;



@Controller
public class LoginController {

    @Autowired
    JobSeekerRepository jobSeekerRepository;


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String login(HttpSession session)
    {
        if(session.getAttribute("jobseeker")!=null)
            return "redirect:/jobListing";
        else
            return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String verify_login(HttpServletRequest request,
                               @ModelAttribute("email") String email,
                               @ModelAttribute("password") String password,
                               Model model, HttpSession session) {

        Job_seeker jobseeker = jobSeekerRepository.findByEmail(email);
        if (jobseeker.getPassword().equals(password)) {
            session.setAttribute("jobseeker",jobseeker);
            return "redirect:/";
        }
        model.addAttribute("wrongCredentials",true);
        return "login";
    }

}
