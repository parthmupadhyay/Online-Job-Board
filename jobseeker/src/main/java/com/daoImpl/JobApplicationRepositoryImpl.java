package com.daoImpl;

import com.dao.JobApplicationRepository;
import com.models.Job_application;
import com.models.Job_seeker;
import com.models.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Amruta on 5/15/2017.
 */
public class JobApplicationRepositoryImpl  {

    @Autowired
    JobApplicationRepository jobApplicationRepository;

    Long countByJobseeker(Job_seeker jobseeker){
        return new Long(1);
    }

    Long countByJobseekerAndStatus(Job_seeker jobseeker, int status){
        return new Long(1);
    }

    Long countByJobseekerAndStatusIn(Job_seeker jobseeker, List<Integer> statusList){
        return new Long(1);
    }


    Job_application findByJobseekerAndPosition(Job_seeker jobseeker, Position position){
        return null;
    }

    List<Job_application> findAllByJobseeker(Job_seeker jobseeker){
        return null;
    }

    @Transactional
    Job_application save(Job_application jobApplication){
        jobApplication = jobApplicationRepository.save(jobApplication);
        return jobApplication;
    }
}
