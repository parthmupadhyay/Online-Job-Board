package com.dao;

import com.models.Job_application;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by parth on 5/16/2017.
 */
public interface ApplicationRepository extends JpaRepository<Job_application,Integer> {
}
