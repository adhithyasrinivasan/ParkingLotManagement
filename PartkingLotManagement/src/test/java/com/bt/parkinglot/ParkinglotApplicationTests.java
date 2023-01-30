package com.bt.parkinglot;

import com.bt.parkinglot.controller.ParkingLotController;
import com.bt.parkinglot.entity.Bay;
import com.bt.parkinglot.entity.Floor;
import com.bt.parkinglot.entity.ParkingLot;
import com.bt.parkinglot.repository.BayRepository;
import com.bt.parkinglot.repository.FloorRepository;
import com.bt.parkinglot.repository.ParkingLotRepository;
import com.bt.parkinglot.service.ParkingLotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
class ParkinglotApplicationTests {

	@Mock
	private ParkingLotRepository parkingLotRepository;
	@Mock
	private FloorRepository floorRepository;
	@Mock
	private BayRepository bayRepository;

	@Autowired
	private ParkingLotController parkingLotController;

	@InjectMocks
	private ParkingLotService slotService;
	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		parkingLotRepository = mock(ParkingLotRepository.class);
		floorRepository = mock(FloorRepository.class);
		bayRepository = mock(BayRepository.class);
		slotService = new ParkingLotService(parkingLotRepository,floorRepository,bayRepository);
	}

	@Test
	public void testGetSlot() {
		// Create a mock parking lot with a single floor and a single bay
		ParkingLot parkingLot = new ParkingLot();
		parkingLot.setTotalSlots(1);
		parkingLot.setAvailableSlots(1);
		parkingLot.setName("LOT1");
		parkingLot.setId(1L);
		Floor floor = new Floor();
		floor.setFloorNumber(1);
		floor.setId(1L);
		floor.setAvailableSlots(1);
		Bay bay = new Bay();
		bay.setSize(Bay.Size.small);
		bay.setId(1L);
		bay.setAvailable(true);
		floor.setBays(Arrays.asList(bay));
		parkingLot.setFloors(Arrays.asList(floor));

		// Mock the repository methods to return the mock parking lot
		when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));

		// Call the getSlot method and verify the result
		String slot = slotService.getSlot(1L, Bay.Size.small);
		assertNotNull(slot);
		assertEquals("1-1",slot);
	}

	@Test
	public void testGetSlot_SlotNotFound() {
		// Arrange
		ParkingLot parkingLot = new ParkingLot();
		parkingLot.setTotalSlots(1);
		parkingLot.setAvailableSlots(1);
		parkingLot.setName("LOT1");
		parkingLot.setId(1L);
		Floor floor = new Floor();
		floor.setFloorNumber(1);
		floor.setId(1L);
		floor.setAvailableSlots(1);
		Bay bay = new Bay();
		bay.setSize(Bay.Size.small);
		bay.setId(1L);
		bay.setAvailable(false);
		floor.setBays(Arrays.asList(bay));
		parkingLot.setFloors(Arrays.asList(floor));
		parkingLotController = new ParkingLotController(slotService);
		String expectedMessage = "NO SLOT FOUND";
		when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(parkingLot));
		// Act
		ResponseEntity<Object> result = parkingLotController.getSlot(1L, Bay.Size.small);
		// Assert
		assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
		assertEquals(expectedMessage, result.getBody());
	}
}
