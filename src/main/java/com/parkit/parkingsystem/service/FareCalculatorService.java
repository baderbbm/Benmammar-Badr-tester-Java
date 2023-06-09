package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    /*
    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

       int inHour = ticket.getInTime().getHours();
        int outHour = ticket.getOutTime().getHours();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        int duration = outHour - inHour;

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

*/
    
  /*
    public void calculateFare(Ticket ticket){
        
        final int MINUTES_PER_HOUR = 60;
        final double MILLISECONDS_PER_MINUTE = 1000.0 * 60.0;
        
    if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
        throw new IllegalArgumentException("Out time provided is incorrect: " + ticket.getOutTime());
    }

     long inPark = ticket.getInTime().getTime();
     long  outPark = ticket.getOutTime().getTime();
        
    long durationInMillis = outPark - inPark; 
    double durationInMinutes = durationInMillis / MILLISECONDS_PER_MINUTE;
    switch (ticket.getParkingSpot().getParkingType()){
        case CAR: {
            ticket.setPrice((durationInMinutes/MINUTES_PER_HOUR) * Fare.CAR_RATE_PER_HOUR);
            break;
        }
        case BIKE: {
            ticket.setPrice((durationInMinutes/MINUTES_PER_HOUR) * Fare.BIKE_RATE_PER_HOUR);
            break;
        }
        default: throw new IllegalArgumentException("Unknown Parking Type");
    }
}
    
    */
    
        private static final int FREE_PARKING_DURATION = 30; 
    
    public void calculateFare(Ticket ticket) {
        
         final int MINUTES_PER_HOUR = 60;
        final double MILLISECONDS_PER_MINUTE = 1000.0 * 60.0;
        
        if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
            throw new IllegalArgumentException("Out time provided is incorrect: " + ticket.getOutTime());
        }
        
        long inPark = ticket.getInTime().getTime();
        long  outPark = ticket.getOutTime().getTime(); 
        long durationInMillis = outPark - inPark;
        double durationInMinutes = durationInMillis / MILLISECONDS_PER_MINUTE;
        
        if (durationInMinutes <= FREE_PARKING_DURATION) {
            ticket.setPrice(0); // Retourne un prix de 0 pour une durée inférieure ou égale à 30 minutes
        } else {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice((durationInMinutes / MINUTES_PER_HOUR) * Fare.CAR_RATE_PER_HOUR);
                    break;
                }
                case BIKE: {
                    ticket.setPrice((durationInMinutes / MINUTES_PER_HOUR) * Fare.BIKE_RATE_PER_HOUR);
                    break;
                }
                default: throw new IllegalArgumentException("Unknown Parking Type");
            }
        }
    }

    
}