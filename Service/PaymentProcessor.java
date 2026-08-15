package Service;

import Enum.PaymentStatus;
import Model.Ticket;
import Strategy.Payment.PaymentStrategy;

public class PaymentProcessor {

    PaymentStrategy paymentStrategy;

    public PaymentProcessor(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public boolean processPayment(double amount, Ticket ticket) {
        boolean success = this.paymentStrategy.pay(amount);

        if (success) {
            ticket.setPaymentStatus(PaymentStatus.SUCCESS);
            System.out.println("Payment successful");
        } else {
            System.out.println("Payment failed");
        }

        return success;
    }

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

}