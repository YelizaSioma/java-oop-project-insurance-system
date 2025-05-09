package contracts;

import company.InsuranceCompany;
import objects.Person;
import payment.ContractPaymentData;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * Base class for any insurance contract.
 * Enforces non-null IDs, non-negative coverage, and unique numbers per insurer.
 */
public abstract class AbstractContract{
    private final String contractNumber; //non-empty string not null. //Must be unique within the same insurer
    protected final InsuranceCompany insurer; //not null
    protected final Person policyHolder; //not null
    protected final ContractPaymentData contractPaymentData;
    protected int coverageAmount; //non-negative
    protected boolean isActive;

    /**
     * @param contractNumber           non-null, non-empty, unique inside Insurance Company, final
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
        validateUniqueContractNumber(insurer, contractNumber);

        //assignment
        this.contractNumber = contractNumber;
        this.insurer = insurer;
        this.policyHolder = policyHolder;
        this.contractPaymentData = contractPaymentData;
        this.coverageAmount=coverageAmount;
        this.isActive = true; //indicates that the policy is live. //Attribute is set to true when the contract is created
    }

    //___________Public methods___________
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

    public ContractPaymentData getContractPaymentData(){
        return contractPaymentData;
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

    /**
     * Delegate to company’s payment handler.
     * @param amount must be > 0
     */
    public void pay(int amount){
        insurer.getHandler().pay(this, amount);
    }

    /**
     * Delegate to company’s chargePremiumsOnContracts logic.
     */
    public void updateBalance(){
        insurer.chargePremiumOnContract(this);
    }


    //___________Private helpers___________ validation method for best practice code
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

    private static void validateUniqueContractNumber(InsuranceCompany insurer, String contractNumber) {
        boolean duplicate = insurer.getContracts().stream()
                .anyMatch(c -> c.getContractNumber().equals(contractNumber));
        if (duplicate) {
            throw new IllegalArgumentException("Contract number '" + contractNumber + "' already exists for this insurer");
        }
    }

    //___________Override methods___________
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractContract)) return false;
        AbstractContract other = (AbstractContract) o;
        return contractNumber.equals(other.contractNumber);
    }

    @Override
    public int hashCode() {
        return contractNumber.hashCode();
    }
}
