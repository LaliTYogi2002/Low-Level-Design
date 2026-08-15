package Model;

import Enum.GateType;
import Service.ParkingLot;

public class EntryGate implements Gate {
    String id;
    GateType gateType;
    ParkingLot parkingLot;

    public EntryGate(String id) {
        this.id = id;
        this.gateType = GateType.ENTRY_GATE;
        this.parkingLot = ParkingLot.getInstance();
    }

    public Ticket parkVehicle(Vehicle vehicle) {
        return parkingLot.parkVehicle(vehicle);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public GateType getType() {
        return gateType;
    }

}
