package Strategy.Payment;

public class CardStrategy implements PaymentStrategy {

    @Override
    public boolean pay(double amount) {
        System.out.println("Paid " + amount + " using Card");
        return true;
    }

}
