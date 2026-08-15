package Factory;

import Enum.PricingStrategyType;
import Strategy.Pricing.EventBasedPricingStrategy;
import Strategy.Pricing.PricingStrategy;
import Strategy.Pricing.TimeBasedPricingStrategy;

public class PricingStrategyFactory {
    public static PricingStrategy getPricingStrategy(PricingStrategyType type) {
        switch (type) {
            case TIMEBASED:
                return new TimeBasedPricingStrategy();
            case EVENTBASED:
                return new EventBasedPricingStrategy();
            default:
                throw new IllegalArgumentException("Invalid pricing strategy type: " + type);
        }
    }
}
