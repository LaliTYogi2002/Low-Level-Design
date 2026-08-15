package Model;

import java.util.concurrent.atomic.AtomicBoolean;

import Enum.VehicleType;

/**
 * Represents an individual Parking Spot in the Parking Lot.
 * 
 * THREAD-SAFETY NOTE:
 * Uses AtomicBoolean to manage the occupied state atomically across multiple entry gate threads.
 */
public class ParkingSpot {
    private String id;
    private VehicleType vehicleType;
    private final AtomicBoolean isOccupied;

    public ParkingSpot(String id, VehicleType vehicleType, boolean isOccupied) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.isOccupied = new AtomicBoolean(isOccupied);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    /**
     * Non-blocking query to check if the spot is currently occupied.
     * Note: A 'true' return value means occupied. But calling isOccupied() 
     * followed by setOccupied() is NOT thread-safe! Use tryOccupy() instead.
     */
    public boolean isOccupied() {
        return isOccupied.get();
    }

    /**
     * ATOMIC COMPARE-AND-SET (CAS):
     * 
     * WHY WE USE THIS:
     * When 2 threads (e.g. Gate 1 & Gate 2) try to claim the exact same spot at the same millisecond:
     * - compareAndSet(false, true) asks the CPU hardware:
     *   "If isOccupied is currently FALSE, change it to TRUE in ONE single atomic step and return true.
     *    If isOccupied is ALREADY TRUE, do nothing and return false."
     * 
     * HOW IT PREVENTS RACE CONDITIONS:
     * Only ONE thread will succeed (return true) and get the spot.
     * The other thread gets false and moves on to check other spots or fails gracefully.
     */
    public boolean tryOccupy() {
        return isOccupied.compareAndSet(false, true);
    }

    /**
     * Atomically vacates (frees) the spot when a vehicle unparks.
     */
    public void vacate() {
        isOccupied.set(false);
    }

    public void setOccupied(boolean occupied) {
        isOccupied.set(occupied);
    }
}
