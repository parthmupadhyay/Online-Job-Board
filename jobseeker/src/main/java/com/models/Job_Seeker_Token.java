package com.models;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by karan on 5/12/2017.
 */

@Entity
public class Job_Seeker_Token {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "jobseeker_id")
    private Job_seeker jobseeker;

    private Date expiryDate;

    public Job_Seeker_Token() {
    }

    public Job_Seeker_Token(final String token, final Job_seeker jobseeker) {
        super();

        this.token = token;
        this.jobseeker = jobseeker;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    private Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public static int getEXPIRATION() {
        return EXPIRATION;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Job_seeker getJobseeker() {
        return jobseeker;
    }

    public void setJobseeker(Job_seeker jobseeker) {
        this.jobseeker = jobseeker;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void updateToken(final String token) {
        this.token = token;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

}
