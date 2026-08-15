package Model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import Enum.VehicleType;

import java.util.concurrent.ConcurrentHashMap;

public class ParkingFloor {

    String id;
    Map<String, ParkingSpot> parkingSpotMap;

    public ParkingFloor(String id) {
        this.id = id;
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

    public Optional<ParkingSpot> findAvailableSpot(VehicleType vehicleType) {
        for (ParkingSpot spot : parkingSpotMap.values()) {
            if (spot.getVehicleType() == vehicleType && !spot.isOccupied()) {
                return Optional.of(spot);
            }
        }
        return Optional.empty();
    }

    public Optional<ParkingSpot> allocateSpot(VehicleType vehicleType) {
        for (ParkingSpot spot : parkingSpotMap.values()) {
            if (spot.getVehicleType() == vehicleType && !spot.isOccupied()) {
                if (spot.tryOccupy()) {
                    return Optional.of(spot);
                }
            }
        }
        return Optional.empty();
    }
}