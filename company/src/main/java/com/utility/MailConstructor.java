package com.utility;

import java.util.Locale;

import com.models.Company;
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

    public SimpleMailMessage constructVerificationTokenEmail(
            String contextPath, Locale locale, String token, Company company) {

        Long company_id = company.getId();
        String url = contextPath + "/companyVerify?company_id=" + company_id;
        String message = "\n Your verification token is: " + token + "\n Please Click On the URL below and enter verification token.\n";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(company.getEmail());
        email.setSubject("Job-board: Account Verification");
        email.setText(url + message);
        email.setFrom("CMPE_275");
        return email;

    }
}
