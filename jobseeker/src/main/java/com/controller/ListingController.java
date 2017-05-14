package com.controller;

import com.dao.ListingRepository;
import com.models.Company;
import com.models.Position;
import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
        model.addAttribute("allCompanies",allCompanies);
        model.addAttribute("allPositions",allPositions);
        model.addAttribute("allLocations",allLocations);
        return "joblisting";
    }
}
