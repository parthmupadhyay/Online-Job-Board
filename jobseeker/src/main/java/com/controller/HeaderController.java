package com.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * Created by Amruta on 5/17/2017.
 */
public class HeaderController {

    @RequestMapping(value = "/about-us", method = RequestMethod.GET)
    public String aboutUs(Model model, HttpSession session)
    {

            return "about-us";
    }

    @RequestMapping(value = "/contact-us", method = RequestMethod.GET)
    public String login(Model model, HttpSession session)
    {

            return "contact-us";
    }
}
