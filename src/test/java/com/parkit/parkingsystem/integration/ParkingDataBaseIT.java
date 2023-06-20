package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.*;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.sql.*;
import java.util.Date;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    
    @Mock
    private static Connection connection;
    
    @Mock
    private static PreparedStatement  preparedStatement;
    
    @Mock
    private static ResultSet resultSet;

    @BeforeAll 
    private static void setUp() throws Exception{ 
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
    private static void tearDown(){

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
    
    
   
@Test
    public void testSaveTicket() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(10.0);
        ticket.setInTime(new Timestamp(System.currentTimeMillis()));
       when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.execute()).thenReturn(true);
        assertEquals(true, ticketDAO.saveTicket(ticket));
    }

    @Test
    public void testGetTicket() throws SQLException, ClassNotFoundException {
      //  when(dataBaseTestConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(anyInt())).thenReturn(1);
        when(resultSet.getString(anyInt())).thenReturn("ABCDEF");
        when(resultSet.getDouble(anyInt())).thenReturn(10.0);
        when(resultSet.getTimestamp(anyInt())).thenReturn(new Timestamp(System.currentTimeMillis()));
        String vehicleRegNumber = "ABCDEF";
        assertNotNull(ticketDAO.getTicket(vehicleRegNumber));
    }
    
    @Test
    public void testUpdateTicket() throws SQLException, ClassNotFoundException {
        when(connection.prepareStatement(DBConstants.UPDATE_TICKET)).thenReturn(preparedStatement);
        when(preparedStatement.execute()).thenReturn(true);  
        Ticket ticket = new Ticket();
        ticket.setPrice(10.0);
        ticket.setOutTime(new Date());
        ticket.setId(1);
        assertEquals(true, ticketDAO.updateTicket(ticket));
    }

}
