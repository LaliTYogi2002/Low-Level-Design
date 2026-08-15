package Model;

import java.time.LocalDateTime;

import Enum.PaymentStatus;

public class Ticket {
    private String ticketId;
    private LocalDateTime entryTime;
    private Vehicle vehicle;
    private String floorId;
    private String spotId;
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    public Ticket(String ticketId, LocalDateTime entryTime, Vehicle vehicle, String floorId, String spotId) {
        this.ticketId = ticketId;
        this.entryTime = entryTime;
        this.vehicle = vehicle;
        this.floorId = floorId;
        this.spotId = spotId;
    }

    public String getId() {
        return ticketId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public String getFloorId() {
        return floorId;
    }

    public String getSpotId() {
        return spotId;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

}
