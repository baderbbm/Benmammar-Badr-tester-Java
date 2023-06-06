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
    
    public void calculateFare(Ticket ticket){
    if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
        throw new IllegalArgumentException("Out time provided is incorrect: " + ticket.getOutTime());
    }

     long inPark = ticket.getInTime().getTime();
     long  outPark = ticket.getOutTime().getTime();
        
    long durationInMillis = outPark - inPark; 
    double durationInMinutes = durationInMillis / (1000.0 * 60);

    switch (ticket.getParkingSpot().getParkingType()){
        case CAR: {
            ticket.setPrice((durationInMinutes/60) * Fare.CAR_RATE_PER_HOUR);
            break;
        }
        case BIKE: {
            ticket.setPrice((durationInMinutes/60) * Fare.BIKE_RATE_PER_HOUR);
            break;
        }
        default: throw new IllegalArgumentException("Unknown Parking Type");
    }
}
   
}