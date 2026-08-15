package Factory;

import Enum.VehicleType;
import Model.Bike;
import Model.Car;
import Model.Truck;
import Model.Vehicle;

public class VehicleFactory {

    public static Vehicle getVehicle(String number, VehicleType type) {
        switch (type) {
            case BIKE:
                return new Bike(number, type);
            case CAR:
                return new Car(number, type);
            case TRUCK:
                return new Truck(number, type);
            default:
                throw new IllegalArgumentException("Invalid vehicle type: " + type);
        }
    }

}
