package contracts;

import company.InsuranceCompany;
import objects.Person;
import payment.ContractPaymentData;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class AbstractContract{
    private final String contractNumber; //non-empty string not null. //Must be unique within the same insurer
    protected final InsuranceCompany insurer; //not null
    protected final Person policyHolder; //not null
    protected final ContractPaymentData contractPaymentData;
    protected int coverageAmount; //non-negative
    protected boolean isActive;

    /**
     * @param contractNumber           non-null, non-empty, unique in Insurance Company, final
     * @param insurer                  non-null, final
     * @param policyHolder             non-null, final
     * @param contractPaymentData      final,
     * @param coverageAmount           non-negative
     * isActive                        set to true after AbstractContract created
     *
     * @throws IllegalArgumentException - in constructor - if any validation fails
     * @throws IllegalArgumentException - in setCoverageAmount - if new amount is non-valid
     */
    //Constructor
    public AbstractContract(String contractNumber,
                            InsuranceCompany insurer,
                            Person policyHolder,
                            ContractPaymentData contractPaymentData,
                            int coverageAmount) {
        //validation
        validateAbstractContractParams(contractNumber, insurer, policyHolder);
        validateCoverageAmount(coverageAmount);

        //assignment
        this.contractNumber = contractNumber;
        //unique contract number validation
        if(insurer.getContracts().contains(this)){
            throw new IllegalArgumentException("Contract Number must be unique inside one insurer.");
        }
        this.insurer = insurer;
        this.policyHolder = policyHolder;
        this.contractPaymentData = contractPaymentData;
        this.coverageAmount=coverageAmount;
        this.isActive = true; //indicates that the policy is live. //Attribute is set to true when the contract is created
    }

    //methods
    public String getContractNumber() {
        return contractNumber;
    }

    public Person getPolicyHolder(){
        return policyHolder;
    }

    public InsuranceCompany getInsurer(){
        return insurer;
    }

    public int getCoverageAmount(){
        return coverageAmount;
    }

    public boolean isActive(){
        return isActive;
    }

    public void setInactive(){
        this.isActive = false;
    }

    /**
     * @param coverageAmount must be ≥ 0
     * @throws IllegalArgumentException  if coverageAmount < 0
     */
    public final void setCoverageAmount(int coverageAmount) {
        validateCoverageAmount(coverageAmount);
        this.coverageAmount = coverageAmount;
    }

    public ContractPaymentData getContractPaymentData(){
        return contractPaymentData;
    }

    public void pay(int amount){
        insurer.getHandler().pay(this, amount);
    }

    public void updateBalance(){
        int paymentMultiplier = 1;

        //set payment multiplier accordingly to the premiumPaymentFrequency
        switch(contractPaymentData.getPremiumPaymentFrequency()) {
            case ANNUAL:
                break;
            case SEMI_ANNUAL:
                paymentMultiplier = 2;
                break;
            case QUARTERLY:
                paymentMultiplier = 4;
                break;
            case MONTHLY:
                paymentMultiplier = 12;
                break;
        }

        if(insurer.getCurrentTime().isAfter(contractPaymentData.getNextPaymentTime())){
            contractPaymentData.setOutstandingBalance(contractPaymentData.getOutstandingBalance() + contractPaymentData.getPremium()*paymentMultiplier);
        }
    }

    //private helper validation method for best practice code
    /**
     * @throws IllegalArgumentException if core constructor params are invalid
     */
    private void validateAbstractContractParams(String contractNumber,
                                                InsuranceCompany insurer,
                                                Person policyHolder){
        if (contractNumber == null || contractNumber.isEmpty()) {
            throw new IllegalArgumentException("Contract number cannot be null or empty");
        }
        if (insurer == null) {
            throw new IllegalArgumentException("Insurer cannot be null");
        }
        if (policyHolder == null) {
            throw new IllegalArgumentException("Policy holder cannot be null");
        }
    }

    /**
     * @throws IllegalArgumentException if coverageAmount is negative
     */
    private void validateCoverageAmount(int coverageAmount){
        if (coverageAmount < 0) {
            throw new IllegalArgumentException("Coverage amount cannot be negative");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AbstractContract contract = (AbstractContract) o;
        return this.getContractNumber().equals(contract.getContractNumber());
    }
}
