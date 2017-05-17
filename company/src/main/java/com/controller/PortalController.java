package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * Created by parth on 5/17/2017.
 */
@Controller
public class PortalController
{
    @RequestMapping(value = "/aboutus",method = RequestMethod.GET)
    public String loadAboutUs(HttpSession session)
    {
        return "aboutus";
    }

    @RequestMapping(value = "/contactus",method = RequestMethod.GET)
    public String loadContactUs(HttpSession session)
    {
        return "contactus";
    }
}
