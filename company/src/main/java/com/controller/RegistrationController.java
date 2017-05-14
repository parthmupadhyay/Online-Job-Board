package com.controller;

import com.dao.CompanyRepository;
import com.dao.TokenRepository;
import com.models.Company;
import com.models.Company_token;
import com.utility.MailConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

/**
 * Created by karan on 5/12/2017.
 */

@Controller
@ComponentScan(value = "com.dao")

public class RegistrationController {

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    private MailConstructor mailConstructor;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
        Company company = new Company();

        model.addAttribute("company", company);
        return "registration";
    }


    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String addCompany(@ModelAttribute("company") Company company, HttpServletRequest request, Model model) throws Exception {
        try {
            company.setIsActivated(0);

            if (companyRepository.findByEmail(company.getEmail()) != null) {
                model.addAttribute("emailExists", true);
                return "registration";
            }

            companyRepository.save(company);
            MultipartFile company_logo = (MultipartFile) company.getLogo_url();
            String name = company.getId() + ".png";

            byte[] bytes = company_logo.getBytes();

            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(new File("src/main/resources/static/images/company_logo/" + name)));
            stream.write(bytes);
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String token = UUID.randomUUID().toString();
        Company_token company_token = new Company_token(token, company);
        tokenRepository.save(company_token);
        String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        SimpleMailMessage new_email = mailConstructor.constructVerificationTokenEmail(appUrl, request.getLocale(), token, company);

        mailSender.send(new_email);

        company.setEmail("");
        company.setDescription("");
        company.setHq_address("");
        company.setName("");
        company.setWebsite("");
        company.setPassword("");
        model.addAttribute("verificationEmailSent", true);
        return "registration";
    }


    @RequestMapping(value = "/companyVerify", method = RequestMethod.GET)
    public String verifyCompanyPage(Model model, @RequestParam("company_id") String company_id) {
        model.addAttribute("company_id", company_id);
        return "verify";
    }

    @RequestMapping(value = "/companyVerify", method = RequestMethod.POST)
    public String verifyCompanyToken(HttpServletRequest request,
                                     @ModelAttribute("company_id") Long company_id,
                                     @ModelAttribute("token") String token,
                                     Model model) throws Exception {

        List<Company_token> company_tokens = tokenRepository.findByCompanyAndToken(companyRepository.findOne(company_id), token);
        if (company_tokens.size() == 1) {
            Company company = companyRepository.findOne(company_id);
            company.setIsActivated(1);
            companyRepository.save(company);
            return "login";
        }

        model.addAttribute("incorrectVerificationToken",true);
        return "verify";
    }

}
