package Strategy.Pricing;

import java.time.LocalDateTime;
import Model.Ticket;

public class TimeBasedPricingStrategy implements PricingStrategy {
    private double hourlyRate = 10;

    public TimeBasedPricingStrategy() {
    }

    @Override
    public double calculatePrice(Ticket ticket, LocalDateTime exitTime) {
        // Implement the logic to calculate price based on time
        long hoursParked = java.time.Duration.between(ticket.getEntryTime(), exitTime).toHours();
        return hoursParked * hourlyRate;
    }

}
