package com.dao;

import com.models.Position;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by parth on 5/13/2017.
 */
public interface PositionRepository extends JpaRepository<Position,Long> {
}
