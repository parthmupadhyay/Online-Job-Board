package com.utility;

import java.util.Locale;

import com.models.Company;
import com.models.Job_seeker;
import com.models.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;


/**
 * Created by karan on 5/13/2017.
 */
@Component
public class MailConstructor {
    @Autowired
    private Environment env;

    public SimpleMailMessage constructVerificationJobseekerTokenEmail(
            String contextPath, Locale locale, String token, Job_seeker job_seeker) {

        Long jobseeker_id = job_seeker.getId();
        String url = contextPath + "/jobseekerVerify?jobseeker_id=" + jobseeker_id;
        String message = "\n Your verification token is: " + token + "\n Please Click On the URL below and enter verification token.\n";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(job_seeker.getEmail());
        email.setSubject("Job-board: Account Verification");
        email.setText(url + message);
        email.setFrom("CMPE_275");
        return email;

    }

    public SimpleMailMessage constructApplicationSentEmail( String subject, String primaryMsg, Position position, Job_seeker job_seeker) {

        Long jobseeker_id = job_seeker.getId();
        String secondaryMsg = "\n Your job details are as follows:\n"+
                            "Description:\n" + position.getDescription() +
                            "Responsibilities:\n" + position.getResponsibilities();

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(job_seeker.getEmail());
        email.setSubject(subject);
        email.setText(primaryMsg + secondaryMsg);
        email.setFrom("cmpe275jobportal@gmail.com");
        return email;

    }


}
