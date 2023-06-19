package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.config.DataBaseConfig;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import java.sql.Connection;
import java.sql.SQLException;





import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

    private static DataBaseConfig dataBaseConfig;
    private static Connection connection;

    private static InputStream originalInput;
    private static PrintStream originalOutput;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception {
        inputReaderUtil = new InputReaderUtil();
        originalInput = System.in;
        originalOutput = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
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
        dataBaseConfig = new DataBaseConfig();
    }

    @AfterAll
    private static void tearDown() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
        if (connection != null) {
            dataBaseConfig.closeConnection(connection);
        }
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
    public void testParkingACar() {
        try {
            ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            parkingService.processIncomingVehicle();

            // vérifier que le ticket a été enregistré dans la base de données.
            verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));

            // vérifier que l'état de disponibilité de la place de stationnement a été mis à jour.
            verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));

            // crée un objet savedTicket avec les valeurs attendues pour les attributs 
            Ticket savedTicket = new Ticket();
            savedTicket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
            savedTicket.setVehicleRegNumber("ABCDEF");
            savedTicket.setPrice(0.0);
            savedTicket.setInTime(new Date(System.currentTimeMillis() - (29 * 60 * 1000)));
            savedTicket.setOutTime(null);

            // vérifier que les valeurs enregistrées dans la base de données correspondent aux valeurs attendues.
            verify(ticketDAO, times(1)).saveTicket(eq(savedTicket));

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
            testParkingACar();
            ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            Ticket existingTicket = new Ticket();
            existingTicket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));  // Heure d'entrée il y a une heure

            // processExitingVehicle pour simuler le départ de l'utilisateur récurrent
            parkingService.processExitingVehicle();

            Date outTime = new Date();
            existingTicket.setOutTime(outTime);
            
            // Vérifier que le ticket a été mis à jour avec le bon prix
            Ticket updatedTicket = ticketDAO.getTicket("ABCDEF");
            double expectedPrice = 1.5;
            assertEquals(expectedPrice, updatedTicket.getPrice());

            // Vérifier que le ticket a été mis à jour avec l'heure de sortie
            assertEquals(outTime, updatedTicket.getOutTime());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// tester le calcul du prix d’un ticket via l’appel de processIncomingVehicle 
// et processExitingVehicle dans le cas d’un utilisateur récurrent
    
    @Test
    public void testParkingLotExitRecurringUser() {
        try {
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
            assertEquals(expectedPrice, updatedTicket.getPrice());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /*
    @Test
    public void testGetConnection() throws SQLException {
        try {
            connection = dataBaseConfig.getConnection();
            assertNotNull(connection);
            assertFalse(connection.isClosed());
        } catch (ClassNotFoundException | SQLException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    */
}
