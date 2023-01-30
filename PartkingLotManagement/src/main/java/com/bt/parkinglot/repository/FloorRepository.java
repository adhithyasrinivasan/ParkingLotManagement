package com.bt.parkinglot.repository;

import com.bt.parkinglot.entity.Bay;
import com.bt.parkinglot.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {

}
