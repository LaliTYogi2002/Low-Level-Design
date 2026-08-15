import Model.EntryGate;
import Model.ExitGate;
import Model.ParkingFloor;
import Model.ParkingSpot;
import Model.Ticket;
import Model.Vehicle;
import Service.ParkingLot;
import Enum.PaymentMode;
import Enum.VehicleType;
import Factory.VehicleFactory;

class Main {
    public static void main(String[] args) {
        EntryGate entryGate = new EntryGate("E1");
        ExitGate exitGate = new ExitGate("X1");
        ParkingFloor floor1 = new ParkingFloor("Floor1");
        floor1.addSpot(new ParkingSpot("F1S1", VehicleType.BIKE, false));
        floor1.addSpot(new ParkingSpot("F1S2", VehicleType.CAR, false));
        floor1.addSpot(new ParkingSpot("F1S3", VehicleType.TRUCK, false));
        floor1.addSpot(new ParkingSpot("F1S4", VehicleType.CAR, false));
        ParkingLot.getInstance().addFloor(floor1);

        Vehicle car1 = VehicleFactory.getVehicle("CAR1", VehicleType.CAR);
        Ticket ticket = entryGate.parkVehicle(car1);
        exitGate.unparkVehicle(ticket.getId(), PaymentMode.UPI);
    }
}