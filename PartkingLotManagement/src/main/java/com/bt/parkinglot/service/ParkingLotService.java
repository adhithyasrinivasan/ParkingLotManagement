package com.bt.parkinglot.service;

import com.bt.parkinglot.entity.Bay;
import com.bt.parkinglot.entity.Floor;
import com.bt.parkinglot.entity.ParkingLot;
import com.bt.parkinglot.exception.ResourceNotFoundException;
import com.bt.parkinglot.repository.BayRepository;
import com.bt.parkinglot.repository.FloorRepository;
import com.bt.parkinglot.repository.ParkingLotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bt.parkinglot.domain.SlotResponse;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class ParkingLotService {
    private final ParkingLotRepository parkingLotRepository;
    private final FloorRepository floorRepository;
    private ReentrantLock lock;
    private BayRepository bayRepository;

    @Autowired
    public ParkingLotService(ParkingLotRepository parkingLotRepository, FloorRepository floorRepository,BayRepository bayRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.floorRepository = floorRepository;
        this.bayRepository = bayRepository;
        lock = new ReentrantLock();
    }

    public ParkingLot createParkingLot(ParkingLot parkingLot) {
        return parkingLotRepository.save(parkingLot);
    }

    public Floor createFloor(Long parkingLotId, Floor floor) {
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId).orElseThrow(() -> new ResourceNotFoundException("Parking lot not found with id " + parkingLotId));
        floor.setParkingLot(parkingLot);
        floorRepository.save(floor);
        parkingLot.getFloors().add(floor);
        parkingLotRepository.save(parkingLot);
        return floor;
    }

    public Bay createBay(Long parkingLotId, Long floorId, Bay bay) {
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId).orElseThrow(() -> new ResourceNotFoundException("Parking Lot not found"));
        Floor floor = floorRepository.findById(floorId).orElseThrow(() -> new ResourceNotFoundException("Floor not found"));
        bay.setFloor(floor);
        bay.setAvailable(true);
        bayRepository.save(bay);
        floor.setAvailableSlots(floor.getAvailableSlots()+1);
        parkingLot.setAvailableSlots(parkingLot.getAvailableSlots()+1);
        parkingLot.setTotalSlots(parkingLot.getTotalSlots()+1);
        parkingLotRepository.save(parkingLot);
        floorRepository.save(floor);
        return bay;
    }

    @Transactional
    public String getSlot(Long parkingLotId, Bay.Size size) throws ResourceNotFoundException {
        // first check if the parking lot exists
        lock.lock();
        try {
            String slot = "";
            SlotResponse response = new SlotResponse();
            if (size.equals(Bay.Size.small)) {
                slot = getSlotBySize(parkingLotId, size);
                if (slot.equals("")) {
                    size = Bay.Size.medium;
                    slot = getSlot(parkingLotId, size);
                }
            } else if (size.equals(Bay.Size.medium)) {
                slot = getSlotBySize(parkingLotId, size);
                if (slot.equals("")) {
                    size = Bay.Size.large;
                    slot = getSlot(parkingLotId, size);
                }
            }
            else if (size.equals(Bay.Size.large)) {
                slot = getSlotBySize(parkingLotId, size);
                if (slot.equals("")) {
                    size = Bay.Size.xlarge;
                    slot = getSlot(parkingLotId, size);
                }
            }
            else {
                slot = getSlotBySize(parkingLotId, size);
                if(slot.equals("")){
                    slot="NO SLOT FOUND";
                }
            }
            return slot;
        }
        finally {
            lock.unlock();
        }

    }

    private String getSlotBySize(Long parkingLotId, Bay.Size size) {
        String slot = "";
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId).orElse(new ParkingLot());
        if (parkingLot.getAvailableSlots()==0) {
            return "";
        }
        List<Floor> floors = parkingLot.getFloors();
        List<Floor> floorsCopy = new ArrayList<>(floors);
        Iterator<Floor> iterator = floorsCopy.iterator();
        while (iterator.hasNext()) {
            Floor floor = iterator.next();
            if(floor.getAvailableSlots()>0) {
                List<Bay> bays = floor.getBays();
                for (Bay bay : bays) {
                    if (bay.getSize().equals(size) && bay.getAvailable()) {
                        bay.setAvailable(false);
                        parkingLot.setAvailableSlots(parkingLot.getAvailableSlots()-1);
                        floor.setAvailableSlots(floor.getAvailableSlots()-1);
                        bayRepository.save(bay);
                        parkingLotRepository.save(parkingLot);
                        floorRepository.save(floor);
                        slot = floor.getId() + "-" + bay.getId();
                        break;
                    }
                }
            }
        }

        return slot;
    }

    public void releaseSlot(Long parkingLotId, Long slotId) {
        lock.lock();
        try {
            ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId).orElseThrow(() -> new ResourceNotFoundException("Parking Lot not found with id : " + parkingLotId));
            List<Floor> floors = parkingLot.getFloors();
            List<Floor> floorsCopy = new ArrayList<>(floors);
            Iterator<Floor> iterator = floorsCopy.iterator();
            while (iterator.hasNext()) {
                Floor floor = iterator.next();
                for (Bay bay : floor.getBays()) {
                    if (bay.getId().equals(slotId)) {
                        bay.setAvailable(true);
                        floor.setAvailableSlots(floor.getAvailableSlots() + 1);
                        parkingLot.setAvailableSlots(parkingLot.getAvailableSlots() + 1);
                        bayRepository.save(bay);
                        parkingLotRepository.save(parkingLot);
                        floorRepository.save(floor);
                        return;
                    }
                }
            }
        }
        finally {
            lock.unlock();
        }
        throw new ResourceNotFoundException("Slot not found with id : " + slotId);
    }
}