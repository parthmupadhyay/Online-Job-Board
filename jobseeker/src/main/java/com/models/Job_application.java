package com.models;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

/**
 * Created by karan on 5/12/2017.
 */

@Entity
public class Job_application {

    @Id
    @Column(name="application_id")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Transient
    private MultipartFile resume_file;

    private String resume_url;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_id")
    private Position position;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "jobseeker_id")
    private Job_seeker jobseeker;

    private int status; //0 : Pending, 1: Offered, 2 :Rejected, 3:  OfferAccepted, 4: OfferRejcted, 5: Cancelled

    public Job_application(){

    }

    public Job_application(Position position , Job_seeker jobseeker, int status){
            this.position = position;
            this.jobseeker = jobseeker;
            this.status = status;
    }

    public Job_application(Position position , Job_seeker jobseeker, int status, MultipartFile file,String url){
        this.position = position;
        this.jobseeker = jobseeker;
        this.status = status;
        this.resume_url = url;
        this.resume_file =file;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MultipartFile getResume_file() {
        return resume_file;
    }

    public void setResume_file(MultipartFile resume_file) {
        this.resume_file = resume_file;
    }

    public String getResume_url() {
        return resume_url;
    }

    public void setResume_url(String resume_url) {
        this.resume_url = resume_url;
    }

    public Job_seeker getJobseeker() {
        return jobseeker;
    }

    public void setJobseeker(Job_seeker jobseeker) {
        this.jobseeker = jobseeker;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Job_seeker getJob_seeker() {
        return jobseeker;
    }

    public void setJob_seeker(Job_seeker job_seeker) {
        this.jobseeker = job_seeker;
    }
}
