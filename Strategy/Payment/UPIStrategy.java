package Strategy.Payment;

public class UPIStrategy implements PaymentStrategy {

    @Override
    public boolean pay(double amount) {
        System.out.println("Paid " + amount + " using UPI");
        return true;
    }

}
