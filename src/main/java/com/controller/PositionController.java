package com.controller;

import com.dao.CompanyRepository;
import com.dao.PositionRepository;
import com.models.Company;
import com.models.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
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

    @RequestMapping(value = "/postjob",method = RequestMethod.GET)
    public String loadPostJob(Model model)
    {
        Position position= new Position();
        model.addAttribute("position", position);

        return "postjob";
    }

    @RequestMapping(value = "/postjob",method = RequestMethod.POST)
    public String addNewPosition(@ModelAttribute("position") Position position)
    {
        System.out.print("in post job: "+position.getLocation());

        Company company = companyRepository.findOne((long)1);//get company id from session
        position.setCompany(company);
        positionRepository.save(position);

        return "postjob";
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
    public String loadUpdatePosition(@ModelAttribute("position") Position position)
    {
        Position updatePosition=positionRepository.findOne(position.getId());
        updatePosition.setDescription(position.getDescription());
        updatePosition.setLocation(position.getLocation());
        updatePosition.setResponsibilities(position.getResponsibilities());
        updatePosition.setSalary(position.getSalary());
        updatePosition.setTitle(position.getTitle());
        positionRepository.save(updatePosition);
        return "login";//should redirect to posted jobs page
    }

    @RequestMapping(value = "/viewjobs",method = RequestMethod.GET)
    public String viewPositions(Model model, HttpSession session)
    {
        Company company_id = (Company)session.getAttribute("company");
        Company company=companyRepository.findOne((long)company_id.getId());//get company id from session
        List<Position> positionList=company.getPositions();
        model.addAttribute("positionList",positionList);
        return "viewjobs";
    }

    @RequestMapping(value = "/position/{id}", method = RequestMethod.GET)
    public String viewJob(@PathVariable long id,Model model)
    {
        Position position=positionRepository.findOne(id);
        model.addAttribute("position",position);
        return "job";
    }
}