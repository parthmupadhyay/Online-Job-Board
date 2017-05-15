package com.dao;
import com.models.Company;
import com.models.Job_application;
import com.models.Job_seeker;
import com.models.Position;
import org.springframework.data.repository.CrudRepository;

import javax.servlet.Registration;
import java.util.List;

/**
 * Created by karan on 5/12/2017.
 */
public interface JobApplicationRepository extends CrudRepository <Job_application,Long>{

    Long countByJobseeker(Job_seeker jobseeker);

    Long countByJobseekerAndStatus(Job_seeker jobseeker, int status);

    Long countByJobseekerAndStatusIn(Job_seeker jobseeker, List<Integer> statusList);

    //check for any active (not terminated ) application for same postiion adn same jobseeker
    //Job_application findByJobseekerAndStatusAndPosition(Job_seeker jobseeker, int status , Position position);

    Job_application findByJobseekerAndPosition(Job_seeker jobseeker,Position position);

    List<Job_application> findAllByJobseeker(Job_seeker jobseeker);

}
