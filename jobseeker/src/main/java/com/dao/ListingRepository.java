package com.dao;

import com.models.Company;
import com.models.Position;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by karan on 5/13/2017.
 */
public interface ListingRepository extends CrudRepository<Position,Long> {

    @Query("SELECT DISTINCT p.company from Position p")
    List<Company> findDistinctCompany();

    @Query("SELECT DISTINCT p.location from Position p")
    List<String> findDistinctLocation();

    List<Position> findAll();
}


