package Strategy.Pricing;

import java.time.LocalDateTime;
import Model.Ticket;

public class EventBasedPricingStrategy implements PricingStrategy {
    private double eventRate = 20;

    public EventBasedPricingStrategy() {
    }

    @Override
    public double calculatePrice(Ticket ticket, LocalDateTime exitTime) {
        long hoursParked = java.time.Duration.between(ticket.getEntryTime(), exitTime).toHours();
        return hoursParked * 10 * eventRate; // Assuming a default rate of $10 per hour
    }

}
