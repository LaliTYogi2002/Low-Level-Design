package Factory;

import Enum.PaymentMode;
import Strategy.Payment.PaymentStrategy;
import Strategy.Payment.UPIStrategy;
import Strategy.Payment.CardStrategy;

public class PaymentFactory {

    public static PaymentStrategy getPaymentStrategy(PaymentMode paymentMode) {
        switch (paymentMode) {
            case UPI:
                return new UPIStrategy();
            case CREDIT_CARD:
                return new CardStrategy();
            default:
                throw new IllegalArgumentException("Invalid payment mode: " + paymentMode);
        }
    }
}
