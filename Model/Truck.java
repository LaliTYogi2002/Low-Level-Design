package Model;

import Enum.VehicleType;

public class Truck implements Vehicle {
    String number;
    VehicleType type;

    public Truck(String number, VehicleType type) {
        this.number = number;
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public VehicleType getType() {
        return type;
    }
}
