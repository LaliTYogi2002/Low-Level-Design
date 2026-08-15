package Strategy.Pricing;

import java.time.LocalDateTime;
import Model.Ticket;


public interface PricingStrategy {
    double calculatePrice(Ticket ticket, LocalDateTime exitTime);
}
