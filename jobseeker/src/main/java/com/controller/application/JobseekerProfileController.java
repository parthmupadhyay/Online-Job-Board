package com.controller.application;

import com.dao.CompanyRepository;
import com.dao.JobSeekerRepository;
import com.dao.JobSeekerTokenRepository;
import com.models.Job_Seeker_Token;
import com.models.Job_seeker;
import com.utility.MailConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import javax.servlet.http.HttpSession;
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
public class JobseekerProfileController {

    private static final Logger log = LoggerFactory.getLogger(JobseekerProfileController.class);
    @Autowired
    JobSeekerRepository jobseekerRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    private MailConstructor mailConstructor;

    @Autowired
    JobSeekerTokenRepository jobseekertokenRepository;

    @Autowired
    private JavaMailSender mailSender;


    @RequestMapping(value = "/jobseekerProfile/view", method = RequestMethod.GET)
    public String viewProfile(Model model,
                              HttpSession session) {

        //Job_seeker jobseeker = jobseekerRepository.findOne(new Long(1)); //testing
        Job_seeker jobseeker = (Job_seeker) session.getAttribute("jobseeker");
        log.debug("jobseeker to be edited:"+jobseeker.getId());
        model.addAttribute("jobseeker", jobseeker);
        return "updateProfile";
    }

    @RequestMapping(value = "/jobseekerProfile/edit",  method = RequestMethod.POST)
    public String editProfile(Model model, HttpSession session,@ModelAttribute("jobseeker") Job_seeker jobseeker,
                             HttpServletRequest request
                             ) throws Exception {

        log.debug("---------------------inside update profile");
        log.debug("update profiel for id:"+jobseeker.getId());
        Job_seeker jobseeker_ = jobseekerRepository.findOne(jobseeker.getId());
        //only update the info fields
        jobseeker_.setFirst_name(jobseeker.getFirst_name());
        jobseeker_.setLast_name(jobseeker.getLast_name());
        jobseeker_.setIntroduction(jobseeker.getIntroduction());
        jobseeker_.setEducation(jobseeker.getEducation());
        jobseeker_.setSkills(jobseeker.getSkills());
        if(jobseeker.getProfilePic() != null){
            try{
                MultipartFile profilePic = (MultipartFile) jobseeker.getProfilePic();
                String name = jobseeker.getId() + ".png";

                byte[] bytes = profilePic.getBytes();

                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(new File("src/main/resources/static/images/profilePic/" + name)));
                stream.write(bytes);
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        jobseekerRepository.save(jobseeker_);

        //update the page and session with latest data
        session.setAttribute("jobseeker",jobseeker_);
        log.debug("jobseeker  updated:"+jobseeker.getId());
        model.addAttribute("jobseeker", jobseeker);
        return "updateProfile";
    }



}
