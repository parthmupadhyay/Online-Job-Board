package com.controller;

import com.dao.CompanyRepository;
import com.models.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by parth on 5/16/2017.
 */
@Controller
public class CompanyController
{
    @Autowired
    CompanyRepository companyRepository;

    @RequestMapping(value = "/company/profile",method = RequestMethod.GET)
    public String viewCompanyPage(Model model, HttpSession session)
    {
        Company company=(Company) session.getAttribute("company");
        company=companyRepository.findOne(company.getId());
        model.addAttribute("company",company);
        return "companyProfile";
    }


    @RequestMapping(value = "/company/profile/edit",method = RequestMethod.GET)
    public String getCompanyEditPage(Model model, HttpSession session)
    {
        Company company=(Company) session.getAttribute("company");
        model.addAttribute("company",company);
        return "editCompany";
    }

    @RequestMapping(value = "/company/profile/edit",method = RequestMethod.POST)
    public String editCompany(@RequestParam String name,
                              @RequestParam String description,
                              @RequestParam String hq_address,
                              @RequestParam String website,
                              @RequestParam Long id,
                              @RequestParam MultipartFile logo_url, HttpSession session)
    {
        try {
            Company updateCompany = companyRepository.findOne(id);
            updateCompany.setName(name);
            updateCompany.setDescription(description);
            updateCompany.setHq_address(hq_address);
            updateCompany.setWebsite(website);
            String logoName = updateCompany.getId() + ".png";
            saveLogo(logo_url, logoName);
            updateCompany.setLogo_url(logo_url);
            companyRepository.save(updateCompany);
            //model.addAttribute("company",updateCompany);
            return "redirect:/company/profile";
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return "error";
    }

    private void saveLogo(MultipartFile logo,String name) throws IOException
    {


        byte[] bytes = logo.getBytes();

        BufferedOutputStream stream = new BufferedOutputStream(
                new FileOutputStream(new File("src/main/resources/static/images/company_logo/" + name)));
        stream.write(bytes);
        stream.close();
    }


}
