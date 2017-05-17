package com.controller.application;

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
                                 HttpSession session,
                                 Model model,
                                 Principal principal)
    {
        log.debug("------------------inside jobapplication");
        log.debug("position for which is appling is :"+position_id);

        if(position_id != null) {

            Position position = positionRepository.findOne(position_id);
            Company company = position.getCompany();
            Job_seeker jobseeker = (Job_seeker) session.getAttribute("jobseeker");
            //Job_seeker jobseeker = jobSeekerRepository.findOne(new Long(1)); //testing get the user details from session or principal
            log.debug("jobseeker id from session :" +jobseeker.getId());
            Job_application jobApplication = new Job_application();
            jobApplication.setPosition(position);
            jobApplication.setJobseeker(jobseeker);
            model.addAttribute("position", position);
            model.addAttribute("company", company);
            model.addAttribute("jobseeker", jobseeker);
            model.addAttribute("jobApplication", jobApplication);

            //check is already applied to this position by same user
            if(isAlreadyApplied(position,jobseeker)){
                model.addAttribute("applicationExists", true);
                jobApplication.setApplicationExists(true);
               // return "redirect:/applicationError";
            }

            //check if same position has been applied and is in terminal state
             /*jobApplication = jobApplicationRepository.findByJobseekerAndNotStatusAndPosition(jobseeker,5,position);
                if(jobApplication != null){
                    jobApplication.setApplicationExists(true);
                    model.addAttribute("applicationExists", true);
                }*/

            //check if 5 pending applicaitons exist
            if(isApplicationQuotaReached(jobseeker)){
                model.addAttribute("applicationNotAllowed", false);
                jobApplication.setApplicationNotAllowed(true);
               // return "redirect:/applicationError";
            }


            log.debug("------------------end");
        }
        return "jobApplication";
    }


    @RequestMapping(value="/jobApplication/apply",params="action=applyByProfile",method=RequestMethod.POST)
    public String action1( Model model,
                             HttpSession session,
                          @RequestParam("position.id") Long position_id,
                          @RequestParam("jobseeker.id") Long jobseeker_id,
                          @RequestParam("company.id") Long company_id,

                          Principal principal)
    {
        log.debug("Action1 block called");
        log.debug("inside createJobApplication");
        log.debug("position for which is appling is :"+position_id);
        log.debug("jobseeker id is :"+jobseeker_id);
        int status = 0; //pending
        Position position_ = positionRepository.findOne(position_id);
        Job_seeker jobseeker_ = jobSeekerRepository.findOne(jobseeker_id);
        Job_application jobApplication = new Job_application(position_,jobseeker_,status);
        jobApplicationRepository.save(jobApplication);


        String message = "\n Thank you for applying to  " + position_.getCompany().getName() +"!.\n";
        String sub = "Job-board:Job Applied";
        sendApplicationNotification(sub, message, position_,jobseeker_);
        model.addAttribute("applicationEmailSent",true);
        log.debug("------------------end");
         return "redirect:/jobListing";
    }
    @RequestMapping(value="/jobApplication/apply",params="action=applyByResume",method=RequestMethod.POST)
    public String action2(Model model,
                          HttpSession session,@RequestParam("position.id") Long position_id,
                          @RequestParam("jobseeker.id") Long jobseeker_id,
                          @RequestParam("company.id") Long company_id,
                          @ModelAttribute("jobApplication") Job_application jobApplication,
                          Principal principal)
    {
        log.debug("Action2 block called");
        log.debug("inside createJobApplication with resume");
        log.debug("position for which is appling is :"+position_id);
        log.debug("jobseeker id is :"+jobseeker_id);
        String resume_path= "src/main/resources/static/resume/";

        try{
            MultipartFile resume = (MultipartFile) jobApplication.getResume_file();
            String name = jobseeker_id +"-" + position_id + ".pdf";
            jobApplication.setResume_url(name);
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
        Position position = positionRepository.findOne(position_id);
        Job_seeker jobseeker = jobSeekerRepository.findOne(jobseeker_id);

        jobApplication.setPosition(position);
        jobApplication.setJobseeker(jobseeker);
        jobApplication.setStatus(status);

        jobApplicationRepository.save(jobApplication);



        String message = "\n Thank you for applying to  " + position.getCompany().getName() +"!.\n\n";
        String sub = "Job-board:Job Applied";
        sendApplicationNotification(sub, message, position,jobseeker);
        model.addAttribute("applicationEmailSent",true);
        log.debug("------------------end");
        return "redirect:/jobListing";
    }


    @RequestMapping(value="/allApplications" , method=RequestMethod.GET)
    public String getAllApplications(Model model,HttpSession session){

        log.debug("----------inside getallapplciations");
        //Job_seeker jobseeker = jobSeekerRepository.findOne(new Long(1)); //testing
        Job_seeker jobseeker = (Job_seeker) session.getAttribute("jobseeker");
        List<Job_application> allApplications = jobApplicationRepository.findAllByJobseeker(jobseeker);
        log.debug("all applications size:"+allApplications.size());
        List<Long> selectedApplications = new ArrayList<>();
        model.addAttribute("allApplications",allApplications);
        model.addAttribute("jobseeker", jobseeker);
        model.addAttribute("selectedApplications",selectedApplications);
        return "appliedJobListing";
    }

    @RequestMapping(value="/jobApplication/changeStatus" , method=RequestMethod.POST)
    public String cancelOrRejectApplication( Model model, HttpSession session,
                                            @RequestParam(value="action", required=true) String action,
                                           @RequestParam(value="id") String[] selectedApplications,
                                            @RequestParam("jobseeker.id") Long jobseeker_id){

        log.debug("-------------inside cancel or reject jobapplcaition");
        log.debug("jobseeker id is :"+jobseeker_id);
        log.debug("appcliation selected size:"+selectedApplications.length);
        Job_seeker jobseeker = jobSeekerRepository.findOne(jobseeker_id);
        switch(action) {
            case "reject":
                log.debug("reject ");
                if(selectedApplications.length > 0) {
                    List<String> notRejected = performRejection(selectedApplications);
                    if (notRejected.size() > 0) {
                        model.addAttribute("notRejected", String.join(",", notRejected));
                    }

                    sendApplciationRejectionMail(selectedApplications, notRejected, jobseeker);
                    model.addAttribute("rejectionMailSent", true);

                }
                break;
            case "cancel":
                log.debug("cancel");
                if(selectedApplications.length > 0) {
                    List<String> notCancelled = performCancellation(selectedApplications);
                    if (notCancelled.size() > 0) {
                        model.addAttribute("notCancelled", String.join(",", notCancelled));
                    }
                    sendApplciationCancellationMail(selectedApplications, notCancelled, jobseeker);
                    model.addAttribute("cancellationMailSent", true);
                }
                break;
            case "accept":
                log.debug("accept");
                if(selectedApplications.length > 0) {
                    List<String> notAccepted = performAccept(selectedApplications);
                    if (notAccepted.size() > 0) {
                        model.addAttribute("notAccepted", String.join(",", notAccepted));
                    }
                    if(selectedApplications.length !=  notAccepted.size())//if not a single offer accepted dont send mail
                    {
                        sendApplciationAcceptMail(selectedApplications, notAccepted, jobseeker);
                        model.addAttribute("acceptOfferMailSent", true);
                    }

                }
                break;
            default:
                // do stuff

                break;
        }

        List<Job_application> allApplications = jobApplicationRepository.findAllByJobseeker(jobseeker);
        log.debug("all applications size:"+allApplications.size());
        model.addAttribute("allApplications",allApplications);
        model.addAttribute("jobseeker", jobseeker);
        return "appliedJobListing";
    }


    //-------------------------------------private methods-------------------------------------------------------------------

    //return error if invalid or success if all can be cancelled
    private List<String> performCancellation(String[] selectedApplications) {
        List<String> notCancelled = new ArrayList<String>();
        for(String id: selectedApplications){
            Job_application jobApplication = jobApplicationRepository.findOne(Long.parseLong(id));
            if(jobApplication.getStatus() != 0){
                notCancelled.add(id);
            }else{
                jobApplication.setStatus(5);//cancelled
                jobApplicationRepository.save(jobApplication);
            }
        }
        return notCancelled;
    }

    //return error if invalid or success if all can be cancelled
    private List<String> performRejection(String[] selectedApplications) {
        List<String> notCancelled = new ArrayList<String>();
        for(String id: selectedApplications){
            Job_application jobApplication = jobApplicationRepository.findOne(Long.parseLong(id));
            if(jobApplication.getStatus() != 1){
                notCancelled.add(id);
            }else{
                jobApplication.setStatus(4);//cancelled
                jobApplicationRepository.save(jobApplication);
            }
        }
        return notCancelled;
    }

    private List<String> performAccept(String[] selectedApplications) {
        List<String> notCancelled = new ArrayList<String>();
        for(String id: selectedApplications){
            Job_application jobApplication = jobApplicationRepository.findOne(Long.parseLong(id));
            if(jobApplication.getStatus() == 1){
                jobApplication.setStatus(3);//offer acccepted
                jobApplicationRepository.save(jobApplication);

            }else{
                notCancelled.add(id);
            }
        }
        return notCancelled;
    }
    private void sendApplciationRejectionMail( String[] selectedIds,  List<String> notSelectedIds , Job_seeker jobSeeker){


        String primaryMsg = "Thank you for your time. Best wishes for your future. " +
                "Please feel free to contact again whenever new position fits you.\n\n ";
        String sub = "JobPortal: Application Offer Rejected";

        String secondaryMsg = (notSelectedIds.size() > 0 )? "\n Applications with application id :"
                + String.join(",",notSelectedIds) +" cannot be rejected.\n": "";
        for(String id : selectedIds){
            if(!notSelectedIds.contains(id)){
                secondaryMsg += "\n Offers with Application id "+id+" rejected.\n";
            }
        }

        secondaryMsg += "\n\n Regards,\n -JobBoard Recruiting.";
        SimpleMailMessage new_email = mailConstructor.constructApplicationSentEmail(sub,primaryMsg, secondaryMsg ,jobSeeker);

        mailSender.send(new_email);
    }

    private void sendApplciationCancellationMail(String[] selectedIds,  List<String> notSelectedIds , Job_seeker jobSeeker){

        String primaryMsg = "Thank you for your response. We hope to see you again. Best wishes!\n\n ";
        String sub = "JobPortal: Application Cancelled";

        String secondaryMsg = (notSelectedIds.size() > 0 )? "\n Applications with application id :"
                + String.join(",",notSelectedIds) +" cannot be cancelled.\n": "";
        for(String id : selectedIds){
            if(!notSelectedIds.contains(id)){
                secondaryMsg += "\n Applciation id "+id+" cancelled.\n";
            }
        }
        secondaryMsg += "\n\n Regards,\n -JobBoard Recruiting.";
        SimpleMailMessage new_email = mailConstructor.constructApplicationSentEmail(sub,primaryMsg, secondaryMsg ,jobSeeker);

        mailSender.send(new_email);
    }

    private void sendApplciationAcceptMail(String[] selectedIds,  List<String> notSelectedIds , Job_seeker jobSeeker){

        String primaryMsg = "Congratulations! Thank you for your acceptance. We are happy to take you onboard!\n\n ";
        String sub = "JobPortal: Application Offer Accepted";

        String secondaryMsg = (notSelectedIds.size() > 0 )? "\n Applications with application id :"
                + String.join(",",notSelectedIds) +" cannot be accepted.\n": "";
        for(String id : selectedIds){
            if(!notSelectedIds.contains(id)){
                secondaryMsg += "\n Application id "+id+" accepted.\n";
            }
        }
        secondaryMsg += "\n\n Regards,\n -JobBoard Recruiting.";
        SimpleMailMessage new_email = mailConstructor.constructApplicationSentEmail(sub,primaryMsg, secondaryMsg ,jobSeeker);

        mailSender.send(new_email);
    }
    private void sendApplicationNotification(String sub ,String primaryMsg, Position position, Job_seeker jobseeker){

        String secondaryMsg = "\nYour job details are as follows:\n"+
                "\nDescription:\n" + position.getDescription() +
                "\nResponsibilities:\n" + position.getResponsibilities()+
                "\n\n Regards,"+
                "\n"+position.getCompany().getName() +" Recruting.";
        SimpleMailMessage new_email = mailConstructor.constructApplicationSentEmail(sub,primaryMsg, secondaryMsg , jobseeker);

        mailSender.send(new_email);

    }

    private boolean isAlreadyApplied(Position position, Job_seeker jobseeker){
        Job_application jobApplication = jobApplicationRepository.findByJobseekerAndPosition(jobseeker,position);
        if(jobApplication != null){
            jobApplication.setApplicationExists(true);
            jobApplication.setApplicationNotAllowed(true);
            return true;

        }
        return false;
    }

    //pending more than 5
    private boolean isApplicationQuotaReached( Job_seeker jobseeker){
        Long applicationCnt = jobApplicationRepository.countByJobseekerAndStatus(jobseeker,0);

        log.debug("total pending count of applications for this user:" + applicationCnt);
        if (applicationCnt >= 5) {
            return true;
        }
        return false;
    }

}
