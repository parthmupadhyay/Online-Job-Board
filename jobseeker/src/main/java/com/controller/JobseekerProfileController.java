package com.controller;

import com.dao.JobSeekerRepository;
import com.dao.JobSeekerTokenRepository;
import com.models.Company;
import com.models.Company_token;
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
    private MailConstructor mailConstructor;

    @Autowired
    JobSeekerTokenRepository jobseekertokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @RequestMapping(value = "/jobseekerProfile", method = RequestMethod.GET)
    public String registration(Model model) {
        Job_seeker jobseeker = new Job_seeker();

        model.addAttribute("jobseeker", jobseeker);
        return "jobseekerProfile";
    }



    @RequestMapping(value = "/jobseekerProfile", method = RequestMethod.POST)
    public String addProfile(@ModelAttribute("jobseeker") Job_seeker jobseeker,
                             HttpServletRequest request,
                             Model model,
                             HttpSession session) throws Exception {
        try {
            jobseeker.setActivated(0);

            if (jobseekerRepository.findByEmail(jobseeker.getEmail()) != null) {
                model.addAttribute("emailExists", true);
                return "jobseekerProfile";
            }

            jobseekerRepository.save(jobseeker);
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
        String token = UUID.randomUUID().toString();
        Job_Seeker_Token jobseeker_Token = new Job_Seeker_Token(token, jobseeker);
        jobseekertokenRepository.save(jobseeker_Token);
        String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        SimpleMailMessage new_email = mailConstructor.constructVerificationJobseekerTokenEmail(appUrl, request.getLocale(), token, jobseeker);

        mailSender.send(new_email);

        jobseeker.setEmail("");
        jobseeker.setEducation("");
        jobseeker.setIntroduction("");
        jobseeker.setFirst_name("");
        jobseeker.setLast_name("");
        jobseeker.setPassword("");
        model.addAttribute("verificationEmailSent", true);
        return "jobseekerProfile";
    }

    @RequestMapping(value = "/jobseekerProfile/view", method = RequestMethod.GET)
    public String viewProfile(Model model,
                              HttpSession session) {

        Job_seeker jobseeker = jobseekerRepository.findOne(new Long(1)); //testing
        //Job_seeker jobseeker = (Job_seeker) session.getAttribute("jobseeker");
        log.debug("jobseeker to be edited:"+jobseeker.getId());
        model.addAttribute("jobseeker", jobseeker);
        return "updateProfile";
    }

    @RequestMapping(value = "/jobseekerProfile/edit",  method = RequestMethod.POST)
    public String editProfile(@ModelAttribute("jobseeker") Job_seeker jobseeker,
                             HttpServletRequest request,
                             Model model,
                             HttpSession session) throws Exception {

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

        session.setAttribute("jobseeker",jobseeker_);
        log.debug("jobseeker  updated:"+jobseeker.getId());
        model.addAttribute("jobseeker", jobseeker);
        return "updateProfile";
    }


    @RequestMapping(value = "/jobseekerVerify", method = RequestMethod.GET)
    public String verifyJobseekerPage(Model model, @RequestParam("jobseeker_id") String jobseeker_id) {
        model.addAttribute("jobseeker_id", jobseeker_id);
        return "verify";
    }

    @RequestMapping(value = "/jobseekerVerify", method = RequestMethod.POST)
    public String verifyJobseekerToken(HttpServletRequest request,
                                     @ModelAttribute("jobseeker_id") Long jobseeker_id,
                                     @ModelAttribute("token") String token,
                                     Model model) throws Exception {

        List<Job_Seeker_Token> jobseeker_tokens = jobseekertokenRepository.findByJobseekerAndToken(jobseekerRepository.findOne(jobseeker_id), token);
        if (jobseeker_tokens.size() == 1) {
            Job_seeker jobseeker = jobseekerRepository.findOne(jobseeker_id);
            jobseeker.setIsActivated(1);
            jobseekerRepository.save(jobseeker);
            return "login";
        }

        model.addAttribute("incorrectVerificationToken",true);
        return "verify";
    }

}
