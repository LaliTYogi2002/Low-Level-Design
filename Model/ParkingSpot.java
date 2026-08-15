package Model;

import java.util.concurrent.atomic.AtomicBoolean;

import Enum.VehicleType;

public class ParkingSpot {
    String id;
    VehicleType vehicleType;
    AtomicBoolean isOccupied;

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

    public boolean isOccupied() {
        return isOccupied.get();
    }

    public boolean tryOccupy() {
        return isOccupied.compareAndSet(false, true);
    }

    public void vacate() {
        isOccupied.set(false);
    }

    public void setOccupied(boolean occupied) {
        isOccupied.set(occupied);
    }
}
