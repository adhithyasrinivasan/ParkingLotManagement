package com.bt.parkinglot.controller;

import com.bt.parkinglot.domain.SlotResponse;
import com.bt.parkinglot.entity.Bay;
import com.bt.parkinglot.entity.Floor;
import com.bt.parkinglot.entity.ParkingLot;
import com.bt.parkinglot.service.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    @Autowired
    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @PostMapping("/parkinglot")
    public ResponseEntity<ParkingLot> createParkingLot(@RequestBody ParkingLot parkingLot) {
        ParkingLot createdParkingLot = parkingLotService.createParkingLot(parkingLot);
        return new ResponseEntity<>(createdParkingLot, HttpStatus.CREATED);
    }

    @PostMapping("/parkinglot/{parkingLotId}/floor")
    public ResponseEntity<Floor> createFloor(@PathVariable Long parkingLotId, @RequestBody Floor floor) {
        Floor createdFloor = parkingLotService.createFloor(parkingLotId, floor);
        return new ResponseEntity<>(createdFloor, HttpStatus.CREATED);
    }

    @PostMapping("/parkinglot/{parkingLotId}/floor/{floorId}/bay")
    public ResponseEntity<Bay> createBay(@PathVariable Long parkingLotId, @PathVariable Long floorId, @RequestBody Bay bay) {
        Bay createdBay = parkingLotService.createBay(parkingLotId, floorId, bay);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBay);
    }

    @PostMapping("/getslot/{parkingLotId}/{size}")
    public ResponseEntity<Object> getSlot(@PathVariable Long parkingLotId, @PathVariable Bay.Size size) {
        String entity = parkingLotService.getSlot(parkingLotId, size);
        if(entity=="NO SLOT FOUND") {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(entity);
        }
        SlotResponse response = new SlotResponse();
        response.setSlot(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/releaseslot/{parkingLotId}/{slotId}")
    public ResponseEntity<Object> releaseSlot(@PathVariable Long parkingLotId, @PathVariable Long slotId) {
        parkingLotService.releaseSlot(parkingLotId,slotId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
