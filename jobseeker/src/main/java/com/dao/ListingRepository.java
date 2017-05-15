package com.dao;

import com.models.Company;
import com.models.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karan on 5/13/2017.
 */
public interface ListingRepository extends CrudRepository<Position, Long> {

    @Query("SELECT DISTINCT p.company from Position p")
    List<Company> findDistinctCompany();

    @Query("SELECT DISTINCT p.location from Position p")
    List<String> findDistinctLocation();

    List<Position> findAll();

    @Query("SELECT p from Position p WHERE p.location in (:location) and p.company.id in (:company) and (p.title like %:query% or p.company.name like %:query% or p.description like %:query% or p.location like %:query%)")
    List<Position> filterData(@Param("location") List<String> location,@Param("company") List<Long> company,@Param("query") String query);

    @Query("SELECT p from Position p WHERE p.company.id in (:company)")
    List<Position> filterByCompany(@Param("company") List<Long> company);

    @Query("SELECT p from Position p WHERE p.location in (:location)")
    List<Position> filterByLocation(@Param("location") List<String> location);

    @Query("SELECT p from Position p where p.title like %:query% or p.company.name like %:query% or p.description like %:query% or p.location like %:query%")
    List<Position> findByQuery(@Param("query") String query);



}


