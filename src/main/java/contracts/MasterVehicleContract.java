package contracts;

import company.InsuranceCompany;
import objects.LegalForm;
import objects.Person;

import java.util.LinkedHashSet;
import java.util.Set;

public class MasterVehicleContract extends AbstractVehicleContract{
    //attributes
    private final Set<SingleVehicleContract> childContracts;

    /**
     * @param policyHolder      must be LEGAL person
     *
     * @throws IllegalArgumentException if the policyHolder is not a legal entity
     */
    //constructor
    public MasterVehicleContract(String contractNumber,
                                 InsuranceCompany insurer,
                                 Person beneficiary,
                                 Person policyHolder){
        //set contractPaymentData to null and coverageAmount to 0
        super(contractNumber, insurer, beneficiary, policyHolder, null, 0);

        //validation
        validatePolicyHolder(policyHolder);  //all policyholders must be legal

        //initialization
        this.childContracts = new LinkedHashSet<>();  //using linked hash set to preserve insertion order
    }

    //override methods
    @Override
    public boolean isActive() {
        if (childContracts.isEmpty()) {
            return super.isActive();
        }

        for (SingleVehicleContract contract : childContracts) {
            if (contract.isActive()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setInactive() {
        for (SingleVehicleContract contract : childContracts) {
            contract.setInactive();
        }
        super.setInactive();
    }

    @Override
    public void pay(int amount) {
        insurer.getHandler().pay(this, amount);
    }

    @Override
    public void updateBalance() {
        insurer.chargePremiumOnContract(this);
    }

    //methods
    public Set<SingleVehicleContract> getChildContracts() {
        return childContracts;
    }

    public void requestAdditionOfChildContract(SingleVehicleContract contract) {
        insurer.moveSingleVehicleContractToMasterVehicleContract(this, contract);
    }

    //validation helper method
    /**
     * @throws IllegalArgumentException if the policyHolder is not a legal entity
     */
    private void validatePolicyHolder(Person policyHolder) {
        if (policyHolder.getLegalForm() != LegalForm.LEGAL) {
            throw new IllegalArgumentException("The policyholder on the MasterVehicleContract must be a legal entity.");
        }
    }
}
