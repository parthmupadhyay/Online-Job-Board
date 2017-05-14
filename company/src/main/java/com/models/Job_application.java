package com.models;

import javax.persistence.*;

/**
 * Created by karan on 5/12/2017.
 */

@Entity
public class Job_application {

    @Id
    @Column(name="application_id")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    private String resume_url;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_id")
    private Position position;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "jobseeker_id")
    private Job_seeker job_seeker;

    private int status;//Pending, Offered, Rejected, OfferAccepted, OfferRejcted, or Cancelled

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResume_url() {
        return resume_url;
    }

    public void setResume_url(String resume_url) {
        this.resume_url = resume_url;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Job_seeker getJob_seeker() {
        return job_seeker;
    }

    public void setJob_seeker(Job_seeker job_seeker) {
        this.job_seeker = job_seeker;
    }
}
