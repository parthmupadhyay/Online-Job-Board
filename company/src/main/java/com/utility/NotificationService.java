package com.utility;

import com.models.Company;
import com.models.Job_seeker;
import com.models.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by parth on 5/14/2017.
 */
@Service
public class NotificationService
{
    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendNotificaitoin(Job_seeker seeker, Position position, Company company,String message) throws MailException, InterruptedException {

        System.out.println("Sleeping now...");
        Thread.sleep(10000);

        System.out.println("Sending email...");

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(seeker.getEmail());
        mail.setFrom(company.getEmail());
        mail.setSubject(company.getName()+" : "+position.getTitle());
        mail.setText(message);
        mailSender.send(mail);

        System.out.println("Email Sent!");
    }
}
