package com.controller;

import com.dao.CompanyRepository;
import com.models.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by karan on 5/12/2017.
 */


@Controller
public class LoginController {

    @Autowired
    CompanyRepository companyRepository;


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String login(HttpSession session)
    {
        if(session.getAttribute("company")!=null)
            return "redirect:/viewjobs";
        else
            return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String verify_login(HttpServletRequest request,
                               @ModelAttribute("email") String email,
                               @ModelAttribute("password") String password,
                               Model model, HttpSession session) {

        Company company = companyRepository.findByEmail(email);
        if (company.getPassword().equals(password)) {
            session.setAttribute("company",company);
            return "redirect:/viewjobs";
        }
        model.addAttribute("wrongCredentials",true);
        return "login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(Model model, HttpSession session)
    {
        if(session.getAttribute("jobseeker")!=null)
            session.removeAttribute("jobseeker");

        return "login";
    }

}
