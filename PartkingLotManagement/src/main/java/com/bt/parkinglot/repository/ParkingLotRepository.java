package com.bt.parkinglot.repository;

import com.bt.parkinglot.entity.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {

}
