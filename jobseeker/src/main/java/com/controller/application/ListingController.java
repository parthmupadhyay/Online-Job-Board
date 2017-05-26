package com.controller.application;

import com.dao.JobSeekerRepository;
import com.dao.ListingRepository;
import com.daoImpl.ListingRepositoryImpl;
import com.models.Company;
import com.models.Job_seeker;
import com.models.Position;
import com.models.SearchClass;
import com.sun.deploy.net.HttpResponse;
import javafx.geometry.Pos;
import org.apache.catalina.Session;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    @Autowired
    JobSeekerRepository jobSeekerRepository;

    @Autowired
    private ListingRepositoryImpl ListingRepositoryImpl;

    @RequestMapping(value = "/jobListing", method = RequestMethod.GET)
    public String jobListing(Model model, HttpSession session, @RequestParam(name = "p", defaultValue = "1") int pageNumber) {
        List<Company> allCompanies = listingRepository.findDistinctCompany();
        List<String> allLocations = listingRepository.findDistinctLocation();
        model.addAttribute("allCompanies", allCompanies);
        model.addAttribute("allLocations", allLocations);
        List<Position> allPositions = ListingRepositoryImpl.getPage(pageNumber);
        //job seeker interested position code
        Job_seeker jobseeker = (Job_seeker) session.getAttribute("jobseeker");
        jobseeker = jobSeekerRepository.findOne(jobseeker.getId());
        List<Position> allInterestedPositions = jobseeker.getInterestedPositions();

        model.addAttribute("allInterestedPosition", allInterestedPositions);
        model.addAttribute("allPositions", allPositions);
        return "joblisting";
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST , consumes = MediaType.APPLICATION_JSON_VALUE)
    public String searchJobListing(Model model, HttpSession session, HttpResponse response, @RequestBody SearchClass searchClass, final RedirectAttributes redirectAttributes) throws Exception {

        System.out.println("Salary:    "+searchClass.getSalary());
        int minPrice=0;
        int maxPrice=Integer.MAX_VALUE;
        if(searchClass.getSalary()!=null)
        {
            if(!searchClass.getSalary().get(0).isEmpty())
                minPrice = Integer.parseInt(searchClass.getSalary().get(0));
            if(!searchClass.getSalary().get(1).isEmpty())
                maxPrice = Integer.parseInt(searchClass.getSalary().get(1));
        }
        else
        {
            minPrice=0;
            maxPrice=Integer.MAX_VALUE;
        }
        List<Company> allCompanies = listingRepository.findDistinctCompany();
        List<String> allLocations = listingRepository.findDistinctLocation();
        List<Position> allPositions=null;
        List<String> searchLocation=searchClass.getLocation();
        List<Long> searchCompany=searchClass.getCompany();
        List<String> searchQuery = searchClass.getSearch();
        String query = searchQuery.get(0);
        if(!searchLocation.isEmpty()&&!searchCompany.isEmpty())
        {
            allPositions = listingRepository.filterData(searchClass.getLocation(),searchClass.getCompany(),query,minPrice,maxPrice);
        }
        else if(!searchLocation.isEmpty())
        {
            allPositions=listingRepository.filterByLocation(searchLocation,query,minPrice,maxPrice);
        }
        else if(!searchCompany.isEmpty())
        {
            allPositions=listingRepository.filterByCompany(searchCompany,query,minPrice,maxPrice);
        }
        else if(searchClass.getSalary()!=null||!query.isEmpty())
        {
            allPositions=listingRepository.findByQuery(query,minPrice,maxPrice);
        }
        else
            allPositions=listingRepository.findAll();
        session.setAttribute("allCompanies", allCompanies);
        session.setAttribute("allLocations", allLocations);
        session.setAttribute("allPositions", allPositions);
        return "joblisting";
    }

    /*@RequestMapping(value = "/search/query",method = RequestMethod.POST)
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
    }*/

    @RequestMapping(value = "/joblisting")
    public String jobListingAfterFilters(Model model, HttpSession session) {

        model.addAttribute("allCompanies", session.getAttribute("allCompanies"));
        model.addAttribute("allLocations", session.getAttribute("allLocations"));
        model.addAttribute("allPositions", session.getAttribute("allPositions"));
        return "joblisting";
    }
}
