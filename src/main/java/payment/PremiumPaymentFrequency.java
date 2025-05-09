package payment;

public enum PremiumPaymentFrequency {
    ANNUAL,
    SEMI_ANNUAL,
    QUARTERLY,
    MONTHLY;

    public int getValueInMonths(){
        return switch (this) {
            case ANNUAL -> 12;
            case SEMI_ANNUAL -> 6;
            case QUARTERLY -> 3;
            case MONTHLY -> 1;
        };
    }
}
