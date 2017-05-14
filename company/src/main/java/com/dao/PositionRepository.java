package com.dao;

import com.models.Company;
import com.models.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by parth on 5/13/2017.
 */
public interface PositionRepository extends JpaRepository<Position,Long>
{
    @Query("select p from Position p where p.company=(:company) and p.status=(:status)")
    List<Position> findByStatusAndCompany(@Param("status") int status,
                                          @Param("company")Company company);
}
