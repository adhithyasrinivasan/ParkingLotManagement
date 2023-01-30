package com.bt.parkinglot.repository;

import com.bt.parkinglot.entity.Bay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BayRepository extends JpaRepository<Bay, Long> {

}
