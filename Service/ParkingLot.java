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

/**
 * Main Singleton Service managing the entire Parking Lot operations.
 * 
 * CONCURRENCY DESIGN CHOICES:
 * 1. Double-Checked Locking with volatile keyword for thread-safe Singleton initialization.
 * 2. ConcurrentHashMap for activeTickets and parkingFloorMap to handle concurrent reads/writes cleanly.
 * 3. Delegating atomic spot allocation to ParkingFloor.allocateSpot() (CAS).
 */
public class ParkingLot {
    // 'volatile' ensures changes made by one thread to parkingLot instance are immediately visible to all other threads.
    private static volatile ParkingLot parkingLot;
    
    // ConcurrentHashMap provides thread-safe bucket-level locking for maps without locking the entire map.
    private final Map<String, ParkingFloor> parkingFloorMap;
    private final Map<String, Ticket> activeTickets;
    private PricingStrategy pricingStrategy;

    private ParkingLot() {
        this.pricingStrategy = PricingStrategyFactory.getPricingStrategy(PricingStrategyType.TIMEBASED);
        this.parkingFloorMap = new ConcurrentHashMap<>();
        this.activeTickets = new ConcurrentHashMap<>();
    }

    /**
     * THREAD-SAFE SINGLETON (Double-Checked Locking):
     * 
     * WHY WE DO THIS:
     * - First 'if' check avoids synchronized block overhead once the instance is initialized (99.9% of calls).
     * - Second 'if' inside 'synchronized' block ensures only ONE thread creates the instance if 2 threads 
     *   reachgetInstance() simultaneously during startup.
     */
    public static ParkingLot getInstance() {
        if (parkingLot == null) { // First check (no lock)
            synchronized (ParkingLot.class) { // Lock only during initial creation
                if (parkingLot == null) { // Second check (with lock)
                    parkingLot = new ParkingLot();
                }
            }
        }
        return parkingLot;
    }

    public void addFloor(ParkingFloor floor) {
        parkingFloorMap.put(floor.getId(), floor);
    }

    /**
     * Entry method called by EntryGates to park a vehicle.
     * Uses atomic floor.allocateSpot(vehicle.getType()) so that multiple entry gates can process
     * incoming vehicles concurrently without assigning the same spot to two vehicles.
     */
    public Ticket parkVehicle(Vehicle vehicle) {
        for (ParkingFloor floor : parkingFloorMap.values()) {
            // Atomically find AND claim a spot in one step
            Optional<ParkingSpot> spotOpt = floor.allocateSpot(vehicle.getType());
            if (spotOpt.isPresent()) {
                ParkingSpot spot = spotOpt.get();
                String ticketId = UUID.randomUUID().toString();
                String floorId = floor.getId();
                String spotId = spot.getId();
                Ticket ticket = new Ticket(ticketId, LocalDateTime.now(), vehicle, floorId, spotId);
                
                // Thread-safe map insertion
                activeTickets.put(ticketId, ticket);
                System.out.println("Vehicle parked. Ticket: " + ticketId + " Spot: " + spotId);
                return ticket;
            }
        }

        System.out.println("No spot available for vehicle type: " + vehicle.getType());
        return null;
    }

    /**
     * Exit method called by ExitGates to unpark a vehicle.
     * Calculates pricing, processes payment, removes the active ticket, and vacates the spot atomically.
     */
    public void unparkVehicle(String ticketId, PaymentMode paymentMode) {
        if (activeTickets.containsKey(ticketId)) {
            Ticket ticket = activeTickets.get(ticketId);
            double amount = pricingStrategy.calculatePrice(ticket, LocalDateTime.now());
            PaymentStrategy paymentStrategy = PaymentFactory.getPaymentStrategy(paymentMode);
            PaymentProcessor paymentProcessor = new PaymentProcessor(paymentStrategy);
            
            if (paymentProcessor.processPayment(amount, ticket)) {
                activeTickets.remove(ticketId);

                // VACATE SPOT: Safely reset spot state so future vehicles can park in it
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
