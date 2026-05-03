public abstract class PaymentFramework {

    protected double amount;
    protected double finalAmount;

    public PaymentFramework(double amount) {
        this.amount = amount;
    }

    public abstract boolean validatePayment();
    public abstract double applyDiscount();

    public void applyVAT() {
        finalAmount = amount * 1.12;
    }

    public void finalizeTransaction() {
        System.out.println("=== Adventurer Guild Reservation Payment ===");
        System.out.println("Original Amount: " + amount);
        System.out.println("Final Amount (with VAT & Discount): " + finalAmount);
        System.out.println("Payment Successful!");
    }

    public boolean processInvoice() {
        if (validatePayment()) {
            applyVAT();
            finalAmount -= applyDiscount();
            finalizeTransaction();
            return true;
        } else {
            System.out.println("Payment Failed.");
            return false;
        }
    }
}
