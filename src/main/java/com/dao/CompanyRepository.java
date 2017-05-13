package com.dao;
import com.models.Company;
import org.springframework.data.repository.CrudRepository;

import javax.servlet.Registration;

/**
 * Created by karan on 5/12/2017.
 */
public interface CompanyRepository extends CrudRepository <Company,Long>{

    Company findByEmail(String email);
}
