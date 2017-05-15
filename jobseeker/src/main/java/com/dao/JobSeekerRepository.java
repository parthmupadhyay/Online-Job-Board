package com.dao;

import com.models.Job_seeker;
import com.models.Position;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by parth on 5/13/2017.
 */
public interface JobSeekerRepository extends JpaRepository<Job_seeker,Long> {

    Job_seeker findByEmail(String email);
}
