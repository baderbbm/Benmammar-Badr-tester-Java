package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

//@ExtendWith(MockitoExtension.class) 
public class FareCalculatorServiceTest {
 
 //  @InjectMocks
    private static FareCalculatorService fareCalculatorService;
  //   @Mock
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }
  
    /*
    vérifie si la méthode "calculateFare" du service "fareCalculatorService" 
    calcule correctement le tarif de stationnement pour une voiture 
    lorsque le temps de stationnement est inférieur à 30 minutes et que le prix calculé est égal à 0
    */   
    
    @Test
    public void calculateFareCarWithLessThan30MinutesParkingTime() {
    // Créer une date d'entrée il y a moins de 30 minutes
    Date inTime = new Date(System.currentTimeMillis() - (29 * 60 * 1000));

    // Créer une date de sortie actuelle
    Date outTime = new Date();

    // Créer un objet ParkingSpot pour une voiture
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    // Créer un ticket avec les informations nécessaires
    Ticket ticket = new Ticket();
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    // Appeler la méthode calculateFare pour calculer le tarif
    fareCalculatorService.calculateFare(ticket);

    // Vérifier que le prix calculé est égal à 0
    assertEquals(0, ticket.getPrice()); 
}
/*
    vérifie si la méthode "calculateFare" du service "fareCalculatorService" 
    calcule correctement le tarif de stationnement pour une moto 
    lorsque le temps de stationnement est inférieur à 30 minutes et que le prix calculé est égal à 0
  */   
    
@Test
    public void calculateFareBikeWithLessThan30MinutesParkingTime() {
    // Créer une date d'entrée il y a moins de 30 minutes
    Date inTime = new Date(System.currentTimeMillis() - (29 * 60 * 1000)); 

    // Créer une date de sortie actuelle
    Date outTime = new Date();

    // Créer un objet ParkingSpot pour une moto
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    // Créer un ticket avec les informations nécessaires
    Ticket ticket = new Ticket();
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    // Appeler la méthode calculateFare pour calculer le tarif
    fareCalculatorService.calculateFare(ticket);

    // Vérifier que le prix calculé est égal à 0
    assertEquals(0, ticket.getPrice()); 
}

/*
    vérifie si la méthode "calculateFare" du service "fareCalculatorService" 
    calcule correctement le tarif de stationnement pour une voiture lorsque le temps de stationnement 
    est supérieur à 30 minutes et que le prix calculé est strictement supérieur à 0.
*/
    
    
public void calculateFareCarWithMoreThan30MinutesParkingTime() {
    // Créer une date d'entrée il y a plus de 30 minutes
    Date inTime = new Date(System.currentTimeMillis() - (31 * 60 * 1000));

    // Créer une date de sortie actuelle
    Date outTime = new Date();

    // Créer un objet ParkingSpot pour une voiture
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    // Créer un ticket avec les informations nécessaires
    Ticket ticket = new Ticket();
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    // Appeler la méthode calculateFare pour calculer le tarif
    fareCalculatorService.calculateFare(ticket);

    // Vérifier que le prix calculé est supérieur à 0
    assertTrue(ticket.getPrice() > 0);
}

/*
    vérifie si la méthode "calculateFare" du service "fareCalculatorService" 
    calcule correctement le tarif de stationnement pour une moto lorsque le temps de stationnement 
    est supérieur à 30 minutes et que le prix calculé est strictement supérieur à 0.
*/
   

public void calculateFareBikeWithMoreThan30MinutesParkingTime() {
    // Créer une date d'entrée il y a plus de 30 minutes
    Date inTime = new Date(System.currentTimeMillis() - (31 * 60 * 1000));

    // Créer une date de sortie actuelle
    Date outTime = new Date();

    // Créer un objet ParkingSpot pour une moto
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    // Créer un ticket avec les informations nécessaires
    Ticket ticket = new Ticket();
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    // Appeler la méthode calculateFare pour calculer le tarif
    fareCalculatorService.calculateFare(ticket);

    // Vérifier que le prix calculé est supérieur à 0
    assertTrue(ticket.getPrice() > 0);
}
   @Test
    public void calculateFareCarWithDiscount() {
        Date inTime = new Date(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        fareCalculatorService.calculateFare(ticket, true);
        double expectedPrice = Fare.CAR_RATE_PER_HOUR * FareCalculatorService.DISCOUNT_FACTOR;
        assertEquals(expectedPrice, ticket.getPrice());
    }
    
        @Test
        public void calculateFareBikeWithDiscount() {
        Date inTime = new Date(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.BIKE, false));
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        fareCalculatorService.calculateFare(ticket, true);
        double expectedPrice = Fare.BIKE_RATE_PER_HOUR * FareCalculatorService.DISCOUNT_FACTOR;
        assertEquals(expectedPrice, ticket.getPrice());
    }

 
  
}
