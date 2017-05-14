package com.models;

import javax.persistence.*;
import java.util.List;

/**
 * Created by karan on 5/12/2017.
 */

@Entity
public class Job_seeker {

    @Id
    @Column(name="jobseeker_id")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;
    private String first_name;
    private String last_name;
    private String introduction;
    private String work_exp;
    private String education;
    private String skills;
    private boolean isActivated;

    @OneToMany(mappedBy = "job_seeker",cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Job_application> jobapplications;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public List<Job_application> getJobapplications() {
        return jobapplications;
    }

    public void setJobapplications(List<Job_application> jobapplications) {
        this.jobapplications = jobapplications;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getWork_exp() {
        return work_exp;
    }

    public void setWork_exp(String work_exp) {
        this.work_exp = work_exp;
    }

    public String getEduction() {
        return education;
    }

    public void setEduction(String eduction) {
        this.education = eduction;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }
}
