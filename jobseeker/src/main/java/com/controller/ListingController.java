package com.controller;

import com.dao.ListingRepository;
import com.models.Company;
import com.models.Position;
import com.models.SearchClass;
import com.sun.deploy.net.HttpResponse;
import javafx.geometry.Pos;
import org.apache.catalina.Session;
import org.json.JSONObject;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by karan on 5/13/2017.
 */


@Controller
public class ListingController {

    @Autowired
    ListingRepository listingRepository;

    @RequestMapping(value = "/jobListing", method = RequestMethod.GET)
    public String jobListing(Model model) {
        List<Company> allCompanies = listingRepository.findDistinctCompany();
        List<Position> allPositions = listingRepository.findAll();
        List<String> allLocations = listingRepository.findDistinctLocation();
        model.addAttribute("allCompanies", allCompanies);
        model.addAttribute("allPositions", allPositions);
        model.addAttribute("allLocations", allLocations);
        return "joblisting";
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String searchJobListing(Model model, HttpResponse response, @RequestBody SearchClass searchClass, final RedirectAttributes redirectAttributes, HttpSession session) throws Exception {

        List<Company> allCompanies = listingRepository.findDistinctCompany();
        List<String> allLocations = listingRepository.findDistinctLocation();
        List<Position> allPositions=null;
        List<String> searchLocation=searchClass.getLocation();
        List<Long> searchCompany=searchClass.getCompany();
        if(!searchLocation.isEmpty()&&!searchCompany.isEmpty())
        {
            allPositions = listingRepository.filterData(searchClass.getLocation(),searchClass.getCompany());
        }
        else if(!searchLocation.isEmpty())
        {
            allPositions=listingRepository.filterByLocation(searchLocation);
        }
        else if(!searchCompany.isEmpty())
        {
            allPositions=listingRepository.filterByCompany(searchCompany);
        }
        else
            allPositions=listingRepository.findAll();
        session.setAttribute("allCompanies", allCompanies);
        session.setAttribute("allLocations", allLocations);
        session.setAttribute("allPositions", allPositions);
        return "joblisting";
    }


    @RequestMapping(value = "/search/query",method = RequestMethod.POST)
    public String searchJobByQuery(@RequestParam String query,HttpSession session,Model model)
    {
        System.out.print("Query:"+query);
        List<Company> allCompanies = listingRepository.findDistinctCompany();
        List<String> allLocations = listingRepository.findDistinctLocation();
        List<Position> allPositions=listingRepository.findByQuery(query);

        model.addAttribute("allCompanies", allCompanies);
        model.addAttribute("allLocations", allLocations);
        model.addAttribute("allPositions", allPositions);
        return "joblisting";
    }

    @RequestMapping(value = "/joblisting")
    public String jobListingAfterFilters(Model model, HttpSession session) {

        model.addAttribute("allCompanies", session.getAttribute("allCompanies"));
        model.addAttribute("allLocations", session.getAttribute("allLocations"));
        model.addAttribute("allPositions", session.getAttribute("allPositions"));
        return "joblisting";
    }
}
