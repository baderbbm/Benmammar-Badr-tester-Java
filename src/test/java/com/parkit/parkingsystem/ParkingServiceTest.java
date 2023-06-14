package com.parkit.parkingsystem;  

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.mockito.Mock;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {
    
    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;
  
    
    private static ParkingService parkingService;
    
    @BeforeEach
    private void setUpPerTest() {
        try {
            
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
           ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
           when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }
/*
    @Test
    public void processExitingVehicleTest(){
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }


    */
    
    @Test
    public void processExitingVehicleTest() {
        when(ticketDAO.getNbTicket(anyString())).thenReturn(2);
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
        verify(ticketDAO, times(1)).getNbTicket(anyString());
    }

/*
    @Test
    public void testProcessIncomingVehicle() { 
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        when(ticketDAO.getNbTicket(anyString())).thenReturn(0);
        parkingService.processIncomingVehicle();
        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
    }


    @Test
    public void processExitingVehicleTestUnableUpdate() {
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
        parkingService.processExitingVehicle();
    verify(parkingSpotDAO, times(0)).updateParking(any(ParkingSpot.class));
    }

   @Test
   public void testGetNextParkingNumberIfAvailable() {
    when(parkingSpotDAO.getNextAvailableSlot(any (ParkingType.class))).thenReturn(1);
    ParkingSpot result = parkingService.getNextParkingNumberIfAvailable();
    assertNotNull(result);
    assertEquals(1, result.getId());
    assertEquals(ParkingType.CAR, result.getParkingType());
    assertTrue(result.isAvailable());
    }

    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(-1);
        ParkingSpot result = parkingService.getNextParkingNumberIfAvailable();
        assertNull(result);
    }
    
    
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("3");
        ParkingSpot result = parkingService.getNextParkingNumberIfAvailable();
        assertNull(result);
    } 
*/
}
