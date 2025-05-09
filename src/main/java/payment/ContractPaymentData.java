package payment;

import objects.Person;

import java.time.LocalDateTime;
import java.util.Set;

public class ContractPaymentData {
    int premium;
    PremiumPaymentFrequency premiumPaymentFrequency;
    LocalDateTime nextPaymentTime;
    int outstandingBalance;

    //constructor
    public ContractPaymentData(int premium, PremiumPaymentFrequency premiumPaymentFrequency, LocalDateTime nextPaymentTime, int outstandingBalance){
        //validation
        validatePremium(premium);
        validatePremiumPaymentFrequency(premiumPaymentFrequency);

        if (nextPaymentTime == null) {
            throw new IllegalArgumentException("Next payment time can't be null in ContractPaymentData.");
        }

        this.premium=premium;
        this.premiumPaymentFrequency=premiumPaymentFrequency;
        this.nextPaymentTime=nextPaymentTime;
        this.outstandingBalance=outstandingBalance;
    }


    //___________Public methods___________
    public int getPremium(){
        return premium;
    }

    public void setPremium(int premium){
        validatePremium(premium);
        this.premium=premium;
    }

    public void setOutstandingBalance (int outstandingBalance){
        this.outstandingBalance=outstandingBalance;
    }

    public int getOutstandingBalance(){
        return outstandingBalance;
    }

    public void setPremiumPaymentFrequency(PremiumPaymentFrequency premiumPaymentFrequency){
        validatePremiumPaymentFrequency(premiumPaymentFrequency);
        this.premiumPaymentFrequency=premiumPaymentFrequency;
    }

    public PremiumPaymentFrequency getPremiumPaymentFrequency(){
        return premiumPaymentFrequency;
    }

    public LocalDateTime getNextPaymentTime(){
        return nextPaymentTime;
    }

    public void updateNextPaymentTime(){
        switch(this.premiumPaymentFrequency.getValueInMonths()) {
            case 12:
                this.nextPaymentTime = this.nextPaymentTime.plusMonths(12);
                break;
            case 6:
                this.nextPaymentTime = this.nextPaymentTime.plusMonths(6);
                break;
            case 3:
                this.nextPaymentTime = this.nextPaymentTime.plusMonths(3);
                break;
            case 1:
                this.nextPaymentTime = this.nextPaymentTime.plusMonths(1);
                break;
            default:
                break;
        }
    }


    //___________Private helpers___________
    private void validatePremium(int premium){
        if (premium <= 0) {
            throw new IllegalArgumentException("Premium value can't be null in ContractPaymentData.");
        }
    }

    private void validatePremiumPaymentFrequency(PremiumPaymentFrequency premiumPaymentFrequency){
        if (premiumPaymentFrequency == null) {
            throw new IllegalArgumentException("Invalid payment frequency.");
        }
    }
}
