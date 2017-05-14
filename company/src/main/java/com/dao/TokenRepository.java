package com.dao;

import com.models.Company;
import com.models.Company_token;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by karan on 5/13/2017.
 */
public interface TokenRepository extends CrudRepository<Company_token,Long> {

    List<Company_token> findByCompanyAndToken(Company company, String token);

}
