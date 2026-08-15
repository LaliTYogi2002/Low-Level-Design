package Service;

import Model.Vehicle;
import Strategy.Payment.PaymentStrategy;
import Strategy.Pricing.PricingStrategy;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import Enum.PaymentMode;
import Enum.PaymentStatus;
import Enum.PricingStrategyType;
import Factory.PaymentFactory;
import Factory.PricingStrategyFactory;
import Model.ParkingFloor;
import Model.Ticket;

public class ParkingLot {
    private static ParkingLot parkingLot;
    private Map<String, ParkingFloor> parkingFloorMap;
    private Map<String, Ticket> activeTickets;
    private PricingStrategy pricingStrategy;

    private ParkingLot() {
        this.pricingStrategy = PricingStrategyFactory.getPricingStrategy(PricingStrategyType.TIMEBASED);
        parkingFloorMap = new HashMap<>();
        activeTickets = new HashMap<>();
    }

    public static ParkingLot getInstance() {
        if (parkingLot == null) {
            parkingLot = new ParkingLot();
        }
        return parkingLot;
    }

    public void addFloor(ParkingFloor floor) {
        parkingFloorMap.put(floor.getId(), floor);
    }

    public Ticket parkVehicle(Vehicle vehicle) {
        for (ParkingFloor floor : parkingFloorMap.values()) {
            if (floor.findAvailableSpot(vehicle.getType()).isPresent()) {
                String ticketId = UUID.randomUUID().toString();
                String floorId = floor.getId();
                String spotId = floor.findAvailableSpot(vehicle.getType()).get().getId();
                Ticket ticket = new Ticket(ticketId, LocalDateTime.now(), vehicle, floorId, spotId);
                activeTickets.put(ticketId, ticket);
                System.out.println("Vehicle parked. Ticket: " + ticketId);
                return ticket;
            }
        }

        System.out.println("No spot available for vehicle type: " + vehicle.getType());
        return null;
    }

    public void unparkVehicle(String ticketId, PaymentMode paymentMode) {
        if (activeTickets.containsKey(ticketId)) {
            Ticket ticket = activeTickets.get(ticketId);
            double amount = pricingStrategy.calculatePrice(ticket, LocalDateTime.now());
            PaymentStrategy paymentStrategy = PaymentFactory.getPaymentStrategy(paymentMode);
            PaymentProcessor paymentProcessor = new PaymentProcessor(paymentStrategy);
            paymentProcessor.processPayment(amount, ticket);
            if (ticket.getPaymentStatus() == PaymentStatus.SUCCESS) {
                activeTickets.remove(ticketId);
                System.out.println("Vehicle unparked. Ticket: " + ticketId);
            } else {
                System.out.println("Payment failed. Please try again.");
            }
        } else {
            System.out.println("Invalid ticket ID: " + ticketId);
        }
    }
}
