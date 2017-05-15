package com.dao;

import com.models.Company;
import com.models.Job_Seeker_Token;
import com.models.Job_seeker;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by karan on 5/13/2017.
 */
public interface JobSeekerTokenRepository extends CrudRepository<Job_Seeker_Token,Long> {

    List<Job_Seeker_Token> findByJobseekerAndToken(Job_seeker job_seeker, String token);

}
