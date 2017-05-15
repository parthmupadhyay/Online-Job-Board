package com.controller;

import com.dao.JobApplicationRepository;
import com.dao.JobSeekerRepository;
import com.dao.PositionRepository;

import com.models.Company;
import com.models.Job_application;
import com.models.Job_seeker;
import com.models.Position;
import com.utility.MailConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;


@Controller
public class JobApplicationController {


    private static final Logger log = LoggerFactory.getLogger(JobApplicationController.class);

    @Autowired
    JobSeekerRepository jobSeekerRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    JobApplicationRepository jobApplicationRepository;

    @Autowired
    private MailConstructor mailConstructor;

    @Autowired

    private JavaMailSender mailSender;

    /*@Autowired
    CompanyRepository companyRepository;*/

    @RequestMapping(value = "/jobApplication", method = RequestMethod.GET)
    public void jobApplication(@PathParam("position_id") Long position_id ,
                        Model model,
                        Principal principal)
    {
        log.debug("------------------inside jobapplication");
        log.debug("position for which is appling is :"+position_id);
        Position position = positionRepository.findOne(position_id);
        Company company = position.getCompany();
        Job_seeker jobseeker = jobSeekerRepository.findOne(new Long(1)); //et the user details from session or principal
        Long applicationCnt = jobApplicationRepository.countByJobseeker(jobseeker);

        log.debug("total count of applications for this user:"+applicationCnt);
        if(applicationCnt >= 5){
            model.addAttribute("applicationNotAllowed",true);
        }

        Job_application jobApplication = new Job_application();
        model.addAttribute("position",position);
        model.addAttribute("company",company);
        model.addAttribute("jobseeker",jobseeker);
        model.addAttribute("jobApplication",jobApplication);
        model.addAttribute("applicationNotAllowed",false);
        log.debug("------------------end");

        //return "jobApplication";
    }

    @RequestMapping(value = "/jobApplication/apply", method = RequestMethod.GET)
    public String createJobApplication(@PathParam("position_id") Long position_id ,
                                 @PathParam("jobseeker_id") Long jobseeker_id ,
                                 Model model,
                                 Principal principal)
    {

        log.debug("inside createJobApplication");
        log.debug("position for which is appling is :"+position_id);
        log.debug("jobseeker id is :"+jobseeker_id);

        //check : if this jobseeker ahs already applied for 5 positions ..dont allow to apply again

        Position position = positionRepository.findOne(position_id);
        Company company = position.getCompany();
        Job_seeker jobseeker = jobSeekerRepository.findOne(jobseeker_id);
        int status = 0; //pending
        Job_application jobApplication = new Job_application(position,jobseeker,status);
        jobApplicationRepository.save(jobApplication);
        model.addAttribute("position",position);
        model.addAttribute("company",company);
        model.addAttribute("jobseeker",jobseeker);
        return "redirect:/jobListing";
    }

    @RequestMapping(value = "/jobApplication/applyResume", method = RequestMethod.GET)
    public String createJobApplicationResume(@PathParam("position_id") Long position_id ,
                                       @PathParam("jobseeker_id") Long jobseeker_id ,
                                             @PathParam("resume_url") MultipartFile resume_url ,
                                       Model model,
                                       Principal principal)
    {

        log.debug("inside createJobApplication with resume");
        log.debug("position for which is appling is :"+position_id);
        log.debug("jobseeker id is :"+jobseeker_id);
        String resume_path= "src/main/resources/static/resume/";
        try{
            MultipartFile resume = (MultipartFile) resume_url;
            String name = jobseeker_id +"-" + position_id + ".pdf";
            log.debug("name is :"+name);
            byte[] bytes = resume.getBytes();
            resume_path +=  name;
            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(new File("src/main/resources/static/resume/" + name)));
            stream.write(bytes);
            stream.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        //check : if this jobseeker ahs already applied for 5 positions ..dont allow to apply again

        Position position = positionRepository.findOne(position_id);
        Company company = position.getCompany();
        Job_seeker jobseeker = jobSeekerRepository.findOne(jobseeker_id);
        int status = 0; //pending
       // Job_application jobApplication = new Job_application(position,jobseeker,status,resume_url);
       // jobApplicationRepository.save(jobApplication);
        model.addAttribute("position",position);
        model.addAttribute("company",company);
        model.addAttribute("jobseeker",jobseeker);
        return "redirect:/jobListing";
    }

    @RequestMapping(value="/jobApplication/apply",params="action=applyByProfile",method=RequestMethod.POST)
    public String action1(@ModelAttribute("company") Company company,
                          @ModelAttribute("position") Position position,
                          @ModelAttribute("jobseeker") Job_seeker jobseeker,
                          Model model,
                          Principal principal)
    {
        log.debug("Action1 block called");
        log.debug("inside createJobApplication");
        log.debug("position for which is appling is :"+position.getId());
        log.debug("jobseeker id is :"+jobseeker.getId());
        int status = 0; //pending
        Job_application jobApplication = new Job_application(position,jobseeker,status);
        jobApplicationRepository.save(jobApplication);

       // sendApplicationNotification(position,jobseeker);
        model.addAttribute("applicationEmailSent",true);
        log.debug("------------------end");
         return "redirect:/jobListing";
    }
    @RequestMapping(value="/jobApplication/apply",params="action=applyByResume",method=RequestMethod.POST)
    public String action2(@ModelAttribute("company") Company company,
                          @ModelAttribute("position") Position position,
                          @ModelAttribute("jobseeker") Job_seeker jobseeker,
                          @ModelAttribute("jobApplication") Job_application jobApplication,
                          Model model,
                          Principal principal)
    {
        log.debug("Action2 block called");
        log.debug("inside createJobApplication with resume");
        log.debug("position for which is appling is :"+position.getId());
        log.debug("jobseeker id is :"+jobseeker.getId());
        String resume_path= "src/main/resources/static/resume/";
        try{
            MultipartFile resume = (MultipartFile) jobApplication.getResume_file();
            String name = jobseeker.getId() +"-" + position.getId() + ".pdf";
            log.debug("name is :"+name);
            byte[] bytes = resume.getBytes();
            resume_path +=  name;
            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(new File("src/main/resources/static/resume/" + name)));
            stream.write(bytes);
            stream.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        //check : if this jobseeker ahs already applied for 5 positions ..dont allow to apply again


        int status = 0; //pending
        jobApplication.setPosition(position);
        jobApplication.setJobseeker(jobseeker);
        jobApplication.setStatus(status);
        jobApplicationRepository.save(jobApplication);

       // sendApplicationNotification(position,jobseeker);
        model.addAttribute("applicationEmailSent",true);
        log.debug("------------------end");
        return "redirect:/jobListing";
    }


    private void sendApplicationNotification(Position position, Job_seeker jobseeker){

        SimpleMailMessage new_email = mailConstructor.constructApplicationSentEmail(position , jobseeker);

        mailSender.send(new_email);

    }

}
