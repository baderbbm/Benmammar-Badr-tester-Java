package com.parkit.parkingsystem.integration;



import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.*;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;



import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;


    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown() {

    }

    /*
    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
    }
    
     */
    // un ticket est actuellement enregistré dans 
    // la base de données et la table du parking est mise à jour avec la disponibilité
    @Test
    public void testParkingACar(){
            assertEquals(0, ticketDAO.getNbTicket("ABCDEF"));
            ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processIncomingVehicle();
            assertEquals(1, ticketDAO.getNbTicket("ABCDEF"));
            Ticket savedTicket = ticketDAO.getTicket("ABCDEF");
            ParkingSpot parkingSpot = savedTicket.getParkingSpot();
            assertEquals(false, parkingSpot.isAvailable());

    }

    /*
  @Test
    public void testParkingLotExit(){
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database
    }
     */
// vérifier que le tarif et l'heure de départ générés sont correctement 
// renseignés dans la base de données
    @Test
    public void testParkingLotExit() {
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        Ticket existingTicket = new Ticket();
        existingTicket.setInTime(new Date(System.currentTimeMillis() - (29 * 60 * 1000)));  // Heure d'entrée il y a une heure
        // processExitingVehicle pour simuler le départ de l'utilisateur récurrent
        parkingService.processExitingVehicle();
        Date outTime = new Date();
        existingTicket.setOutTime(outTime);
        // Vérifier que le ticket a été mis à jour avec le bon prix
        Ticket updatedTicket = ticketDAO.getTicket("ABCDEF");
        assertEquals(1, ticketDAO.getNbTicket("ABCDEF"));
        double expectedPrice = 0;
        assertEquals(expectedPrice, updatedTicket.getPrice());
        //Vérifier que le ticket a été mis à jour avec l'heure de sortie  
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        String expectedOutTime = dateFormat.format(outTime);
        String bddOutTime = dateFormat.format(updatedTicket.getOutTime());
        assertEquals(expectedOutTime, bddOutTime);  

          // assertEquals(outTime, updatedTicket.getOutTime());
    }

// tester le calcul du prix d’un ticket via l’appel de processIncomingVehicle 
// et processExitingVehicle dans le cas d’un utilisateur récurrent
    @Test
    public void testParkingLotExitRecurringUser() {
            ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processIncomingVehicle();
            // Simuler un utilisateur récurrent en ayant déjà un ticket dans la base de données
            Ticket existingTicket = new Ticket();
            existingTicket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
            existingTicket.setVehicleRegNumber("ABCDEF");
            existingTicket.setPrice(0);
            existingTicket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));  // Heure d'entrée il y a une heure
            existingTicket.setOutTime(null);
            ticketDAO.saveTicket(existingTicket);
            // processExitingVehicle pour simuler le départ de l'utilisateur récurrent
            parkingService.processExitingVehicle();
            // Vérifier que le ticket a été mis à jour avec le bon prix (avec remise de 5%)
            Ticket updatedTicket = ticketDAO.getTicket("ABCDEF");
            double expectedPrice = 1.5 * 0.95;
            assertEquals(expectedPrice, updatedTicket.getPrice(), 0.01); 
    }
    }
