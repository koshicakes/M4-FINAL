public class FacilityPayment extends PaymentFramework {
    
    private Adventurer adventurer;
    private String facilityName;
    private Guild guild;

    public FacilityPayment(double amount, Adventurer adventurer, String facilityName, Guild guild) {
        super(amount); 
        this.adventurer = adventurer;
        this.facilityName = facilityName;
        this.guild = guild;
    }

    @Override
    public boolean validatePayment() {
        if (adventurer.getGold() >= (amount * 1.12)) {
            return true;
        }
        System.out.println("Not enough gold! You need at least " + (amount * 1.12) + " Gold.");
        return false;
    }

    @Override
    public double applyDiscount() {
        double discount = 0.0;
        String rank = adventurer.getRank();

        if (rank.equals("S")) {
            discount = finalAmount * 0.20; 
            System.out.println("S-Rank Privilege: 20% Discount applied!");
        } else if (rank.equals("A")) {
            discount = finalAmount * 0.10; 
            System.out.println("A-Rank Privilege: 10% Discount applied!");
        }
        return discount;
    }

    @Override
    public void finalizeTransaction() {
        super.finalizeTransaction();
        
        adventurer.deductGold((int) finalAmount); 
        guild.saveAdventurers(); 

        double vat = amount * 0.12;
        double discount = (amount + vat) - finalAmount;
        DatabaseConnection.logTransaction(adventurer.getName(), facilityName, amount, vat, discount, finalAmount);
    }
}
