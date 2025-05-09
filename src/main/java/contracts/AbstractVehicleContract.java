package contracts;

import company.InsuranceCompany;
import objects.Person;
import payment.ContractPaymentData;

public abstract class AbstractVehicleContract extends AbstractContract {
    protected Person beneficiary;

    /**
     * @param beneficiary           can be null, if non‐null, must not equal policyHolder
     * @param policyHolder          cannot be same person as beneficiary
     *
     * @throws IllegalArgumentException - in constructor - if policyHolder equals beneficiary
     */
    //constructor
    public AbstractVehicleContract(String contractNumber,
                                   InsuranceCompany insurer,
                                   Person beneficiary,
                                   Person policyHolder,
                                   ContractPaymentData contractPaymentData,
                                   int coverageAmount){
        //super-class constructor
        super(contractNumber, insurer, policyHolder, contractPaymentData, coverageAmount);

        validateBeneficiary(beneficiary);
        this.beneficiary=beneficiary;
    }

    /**
     * @param beneficiary may be null, if non‐null, must not equal the current policyHolder
     * @throws IllegalArgumentException if beneficiary equals policyHolder
     */
    public void setBeneficiary(Person beneficiary){
        validateBeneficiary(beneficiary);
        this.beneficiary = beneficiary;
    }

    public Person getBeneficiary(){
        return beneficiary;
    }

    //private helper validation method for best practice code to avoid override subclass methods errors
    private void validateBeneficiary(Person beneficiary){
        if (beneficiary != null && beneficiary.equals(getPolicyHolder())){
            throw new IllegalArgumentException("Beneficiary cannot be policy holder at the same time.");
        }
    }
}
