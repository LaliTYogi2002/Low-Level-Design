package Service;

import Model.Vehicle;
import Strategy.Payment.PaymentStrategy;
import Strategy.Pricing.PricingStrategy;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import Enum.PaymentMode;
import Enum.PaymentStatus;
import Enum.PricingStrategyType;
import Factory.PaymentFactory;
import Factory.PricingStrategyFactory;
import Model.ParkingFloor;
import Model.ParkingSpot;
import Model.Ticket;

public class ParkingLot {
    private static volatile ParkingLot parkingLot;
    private final Map<String, ParkingFloor> parkingFloorMap;
    private final Map<String, Ticket> activeTickets;
    private PricingStrategy pricingStrategy;

    private ParkingLot() {
        this.pricingStrategy = PricingStrategyFactory.getPricingStrategy(PricingStrategyType.TIMEBASED);
        parkingFloorMap = new ConcurrentHashMap<>();
        activeTickets = new ConcurrentHashMap<>();
    }

    public static ParkingLot getInstance() {
        if (parkingLot == null) {
            synchronized (ParkingLot.class) {
                if (parkingLot == null) {
                    parkingLot = new ParkingLot();
                }
            }
        }
        return parkingLot;
    }

    public void addFloor(ParkingFloor floor) {
        parkingFloorMap.put(floor.getId(), floor);
    }

    public Ticket parkVehicle(Vehicle vehicle) {
        for (ParkingFloor floor : parkingFloorMap.values()) {
            Optional<ParkingSpot> spotOpt = floor.allocateSpot(vehicle.getType());
            if (spotOpt.isPresent()) {
                ParkingSpot spot = spotOpt.get();
                String ticketId = UUID.randomUUID().toString();
                String floorId = floor.getId();
                String spotId = spot.getId();
                Ticket ticket = new Ticket(ticketId, LocalDateTime.now(), vehicle, floorId, spotId);
                activeTickets.put(ticketId, ticket);
                System.out.println("Vehicle parked. Ticket: " + ticketId + " Spot: " + spotId);
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
            if (paymentProcessor.processPayment(amount, ticket)) {
                activeTickets.remove(ticketId);

                // Vacate the spot so it becomes available for other vehicles
                ParkingFloor floor = parkingFloorMap.get(ticket.getFloorId());
                if (floor != null) {
                    ParkingSpot spot = floor.getParkingSpotMap().get(ticket.getSpotId());
                    if (spot != null) {
                        spot.vacate();
                    }
                }
                System.out.println("Vehicle unparked. Ticket: " + ticketId);
            } else {
                System.out.println("Payment failed. Please try again.");
            }
        } else {
            System.out.println("Invalid ticket ID: " + ticketId);
        }
    }
}
