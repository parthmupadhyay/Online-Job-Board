package com.models;

import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import javax.persistence.*;
import java.util.List;

/**
 * Created by karan on 5/12/2017.
 */

@Entity
public class Company {

    @Id
    @Column(name = "company_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String website;

    @Transient
    private MultipartFile logo_url;

    private String hq_address;
    private String description;

    @Column(unique = true)
    private String email;

    private String password;
    private int isActivated;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Position> positions;

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

    public int getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(int activated) {
        isActivated = activated;
    }


    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getHq_address() {
        return hq_address;
    }

    public void setHq_address(String hq_address) {
        this.hq_address = hq_address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(MultipartFile logo_url) {
        this.logo_url = logo_url;
    }

    public Company(String name, String website, MultipartFile logo_url, String hq_address, String description, String email, String password, int isActivated) {
        this.name = name;
        this.website = website;
        this.logo_url = logo_url;
        this.hq_address = hq_address;
        this.description = description;
        this.email = email;
        this.password = password;
        this.isActivated = isActivated;
    }

    public Company() {
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", website='" + website + '\'' +
                ", logo_url=" + logo_url +
                ", hq_address='" + hq_address + '\'' +
                ", description='" + description + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isActivated=" + isActivated +
                ", positions=" + positions +
                '}';
    }
}
