package Model;

import Enum.GateType;
import Enum.PaymentMode;
import Service.ParkingLot;

public class ExitGate implements Gate {
    String id;
    GateType gateType;
    ParkingLot parkingLot;

    public ExitGate(String id) {
        this.id = id;
        this.gateType = GateType.EXIT_GATE;
        this.parkingLot = ParkingLot.getInstance();
    }

    public void unparkVehicle(String ticketId, PaymentMode paymentMode) {
        parkingLot.unparkVehicle(ticketId, paymentMode);
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
