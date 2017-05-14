package com.models;

import javax.persistence.*;
import java.util.List;

/**
 * Created by karan on 5/12/2017.
 */

@Entity
public class Position {

    @Id
    @Column(name="position_id")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String title;
    private String description;
    private String location;
    private String responsibilities;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    private Company company;

    private int salary;
    private int status; // 0 : open 1: filled 2: Cancelled

    @OneToMany(mappedBy = "position",cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Job_application> jobapplications;

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
