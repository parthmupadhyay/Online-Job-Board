package com.dao;
import com.models.Company;
import com.models.Job_application;
import com.models.Job_seeker;
import org.springframework.data.repository.CrudRepository;

import javax.servlet.Registration;

/**
 * Created by karan on 5/12/2017.
 */
public interface JobApplicationRepository extends CrudRepository <Job_application,Long>{

    Long countByJobseeker(Job_seeker jobseeker);

}
