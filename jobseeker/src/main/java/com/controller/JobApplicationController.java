package com.controller;

import com.dao.JobApplicationRepository;
import com.dao.JobSeekerRepository;
import com.dao.PositionRepository;
import com.daoImpl.JobApplicationRepositoryImpl;
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

import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@Controller
public class JobApplicationController {


    private static final Logger log = LoggerFactory.getLogger(JobApplicationController.class);

    @Autowired
    JobSeekerRepository jobSeekerRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    JobApplicationRepository jobApplicationRepository;

//    @Autowired
//    JobApplicationRepositoryImpl jobApplicationRepositoryImpl;

    @Autowired
    private MailConstructor mailConstructor;

    @Autowired

    private JavaMailSender mailSender;

    /*@Autowired
    CompanyRepository companyRepository;*/

    @RequestMapping(value = "/jobApplication/open", method = RequestMethod.GET)
    public String jobApplication(@PathParam("position_id") Long position_id ,
                        Model model,
                        Principal principal)
    {
        log.debug("------------------inside jobapplication");
        log.debug("position for which is appling is :"+position_id);

        if(position_id != null) {

            Position position = positionRepository.findOne(position_id);
            Company company = position.getCompany();
            //Job_seeker jobseeker = (Job_seeker) session.getAttribute("jobseeker");
            Job_seeker jobseeker = jobSeekerRepository.findOne(new Long(1)); //testing get the user details from session or principal

            //check is already applied to this position by same user
            Job_application jobApplication = jobApplicationRepository.findByJobseekerAndPosition(jobseeker,position);
            if(jobApplication != null){
                jobApplication.setApplicationExists(true);
                model.addAttribute("applicationExists", true);
            }

            //check if same position has been applied and is in terminal state
             /*jobApplication = jobApplicationRepository.findByJobseekerAndNotStatusAndPosition(jobseeker,5,position);
                if(jobApplication != null){
                    jobApplication.setApplicationExists(true);
                    model.addAttribute("applicationExists", true);
                }*/

            //check if 5 pending applicaitons exist
            Long applicationCnt = jobApplicationRepository.countByJobseekerAndStatus(jobseeker,0);

            log.debug("total pending count of applications for this user:" + applicationCnt);
            if (applicationCnt >= 5) {
                jobApplication.setApplicationNotAllowed(true);
                model.addAttribute("applicationNotAllowed", true);
            }

            jobApplication = new Job_application(position,jobseeker,0);
            model.addAttribute("position", position);
            model.addAttribute("company", company);
            model.addAttribute("jobseeker", jobseeker);
            model.addAttribute("jobApplication", jobApplication);
           // model.addAttribute("applicationNotAllowed", false);
            log.debug("------------------end");
        }
        return "jobApplication";
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
        Position position_ = positionRepository.findOne(position.getId());
        Job_seeker jobseeker_ = jobSeekerRepository.findOne(new Long(1));
        Job_application jobApplication = new Job_application(position_,jobseeker_,status);
        jobApplicationRepository.save(jobApplication);

        List<Job_application> appliedJobs = jobseeker.getJobapplications();
        appliedJobs.add(jobApplication);
        jobseeker.setJobapplications(appliedJobs);
        jobSeekerRepository.save(jobseeker);

        String message = "\n Thank you for applying to  " + position.getCompany().getName() +"!.\n";
        sendApplicationNotification(message, position,jobseeker);
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

        List<Job_application> appliedJobs = jobseeker.getJobapplications();
        appliedJobs.add(jobApplication);
        jobseeker.setJobapplications(appliedJobs);
        jobSeekerRepository.save(jobseeker);

        String message = "\n Thank you for applying to  " + position.getCompany().getName() +"!.\n";
       sendApplicationNotification(message, position,jobseeker);
        model.addAttribute("applicationEmailSent",true);
        log.debug("------------------end");
        return "redirect:/jobListing";
    }


    @RequestMapping(value="/allApplications" , method=RequestMethod.GET)
    public String getAllApplications(HttpSession session, Model model){

        Job_seeker jobseeker = jobSeekerRepository.findOne(new Long(1)); //testing
        //Job_seeker jobseeker = (Job_seeker) session.getAttribute("jobseeker");
        List<Job_application> allApplications = jobApplicationRepository.findAllByJobseeker(jobseeker);
        List<Job_application> selectedApplications = new ArrayList<>();
        model.addAttribute("allApplications",allApplications);
        model.addAttribute("jobseeker", jobseeker);
        model.addAttribute("selectedApplications",selectedApplications);
        return "appliedJobListing";
    }

    @RequestMapping(value="/jobApplication/changeStatus" , method=RequestMethod.POST)
    public String cancelOrrejectApplication(HttpSession session, Model model,
                                            @RequestParam(value="action", required=true) String action,
                                            @ModelAttribute("allApplications") List<Job_application> allApplications){

        log.debug("-------------inside cancel or reject jobapplcaition");
        log.debug("appcliation selected size:"+allApplications.size());
        switch(action) {
            case "reject":
                log.debug("reject ");
                // for each selected jobapplicaiton
                //check if the status is offered only then reject the applciation
                //set  new status and save the applciation
                //send the mail saying u hv rejected the offer
                String primaryMsg = "Thank you for your time. Best wishes for your future. Please feel free to contact again whenever new position fits you. ";
                //Position position = positionRepository.findOne(jobApplication.getPosition().getId());
                //Job_seeker jobseeker = jobSeekerRepository.findOne(jobApplication.getPosition().getId());
                //sendApplicationNotification(primaryMsg,position,jobseeker);
                break;
            case "cancel":
                log.debug("cancel");
                // for each selected jobapplicaiton
                //check if the status is pending only then cancel the applciation
                //set  new status and save the applciation
                break;

            default:
                // do stuff
                break;
        }
        return "appliedJobListing";
    }


    private void sendApplicationNotification(String primaryMsg, Position position, Job_seeker jobseeker){

        SimpleMailMessage new_email = mailConstructor.constructApplicationSentEmail(primaryMsg, position , jobseeker);

        mailSender.send(new_email);

    }

}
