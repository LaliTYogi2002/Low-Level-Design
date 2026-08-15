package Model;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import Enum.VehicleType;

/**
 * Represents a single Parking Floor containing multiple Parking Spots.
 */
public class ParkingFloor {

    private String id;
    private Map<String, ParkingSpot> parkingSpotMap;

    public ParkingFloor(String id) {
        this.id = id;
        // ConcurrentHashMap allows concurrent spot insertions/reads safely
        this.parkingSpotMap = new ConcurrentHashMap<>();
    }

    public void addSpot(ParkingSpot spot) {
        parkingSpotMap.put(spot.getId(), spot);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, ParkingSpot> getParkingSpotMap() {
        return parkingSpotMap;
    }

    public void setParkingSpotMap(Map<String, ParkingSpot> parkingSpotMap) {
        this.parkingSpotMap = parkingSpotMap;
    }

    /**
     * Passive read-only query to find an available spot.
     * WARNING: Calling this followed by parkVehicle() is NOT thread-safe under concurrency.
     * Use allocateSpot() instead for atomic check-and-claim.
     */
    public Optional<ParkingSpot> findAvailableSpot(VehicleType vehicleType) {
        for (ParkingSpot spot : parkingSpotMap.values()) {
            if (spot.getVehicleType() == vehicleType && !spot.isOccupied()) {
                return Optional.of(spot);
            }
        }
        return Optional.empty();
    }

    /**
     * ATOMIC SPOT ALLOCATION:
     * 
     * WHY THIS IS THREAD-SAFE:
     * Combines checking availability AND claiming the spot into ONE step.
     * If 5 threads try to allocate the same spot at the exact same moment:
     * - Only ONE thread's spot.tryOccupy() will return true.
     * - The remaining 4 threads will get false and automatically continue 
     *   their loop to evaluate the next spot!
     */
    public Optional<ParkingSpot> allocateSpot(VehicleType vehicleType) {
        for (ParkingSpot spot : parkingSpotMap.values()) {
            if (spot.getVehicleType() == vehicleType && !spot.isOccupied()) {
                if (spot.tryOccupy()) {
                    return Optional.of(spot); // Successfully claimed!
                }
            }
        }
        return Optional.empty();
    }
}