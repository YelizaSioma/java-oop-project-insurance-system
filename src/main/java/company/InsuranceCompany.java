package company;

import contracts.*;
import objects.Person;
import objects.Vehicle;
import payment.ContractPaymentData;
import payment.PaymentHandler;
import payment.PremiumPaymentFrequency;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

public class InsuranceCompany {
    //attributes
    private final Set<AbstractContract> contracts; //are stored in the order in which they were concluded by the insurer.
    private final PaymentHandler handler;
    private LocalDateTime currentTime;

    //constructor
    public InsuranceCompany(LocalDateTime currentTime){
        validateCurrentTime(currentTime);

        this.contracts = new LinkedHashSet<>();
        this.currentTime=currentTime;
        this.handler = new PaymentHandler(this);
    }

    //additional validation methods
    private void validateCurrentTime(LocalDateTime currentTime){
        if (currentTime == null) {
            throw new IllegalArgumentException("Current time cannot be null in Insurance Company");
        }
    }

    private void validateContractNumber(String contractNumber){
        //unique contract number check
        for (AbstractContract contract : contracts){
            if(contract.getContractNumber().equals(contractNumber)){
                throw new IllegalArgumentException("Contract number should be unique inside of the one Insurance Company");
            }
        }
    }

    //methods
    public LocalDateTime getCurrentTime(){
        return currentTime;
    }

    public void setCurrentTime(LocalDateTime currentTime){
        validateCurrentTime(currentTime);
        this.currentTime=currentTime;
    }

    public Set<AbstractContract> getContracts(){
        return contracts;
    }

    public PaymentHandler getHandler(){
        return handler;
    }

    public SingleVehicleContract insureVehicle(String contractNumber,
                                               Person beneficiary,
                                               Person policyHolder,
                                               int proposedPremium,
                                               PremiumPaymentFrequency proposedPaymentFrequency,
                                               Vehicle vehicleToInsure){
        //parameters validations
        if (policyHolder == null) {
            throw new IllegalArgumentException("Policy holder cannot be null");
        }

        if (proposedPremium <= 0) {
            throw new IllegalArgumentException("Proposed premium must be positive");
        }

        if (proposedPaymentFrequency == null) {
            throw new IllegalArgumentException("Premium payment frequency cannot be null");
        }

        if (vehicleToInsure == null) {
            throw new IllegalArgumentException("Vehicle to insure cannot be null");
        }

        //other business validations
        //unique contract number check
        validateContractNumber(contractNumber);

        // Calculate annual premium based on payment frequency
        int annualPremium = (12 / proposedPaymentFrequency.getValueInMonths()) * proposedPremium;

        //paidOutAmount(Person) >= 2% originalValue(Vehicle) check
        if (annualPremium < (0.02 * vehicleToInsure.getOriginalValue())){
            throw new IllegalArgumentException("The total annual amount paid by the policyholder must be greater than or equal to 2% of the cost of the vehicle.");
        }

        int coverageAmount = vehicleToInsure.getOriginalValue() / 2;

        SingleVehicleContract singleVehicleContract = new SingleVehicleContract(
                contractNumber,
                this,
                beneficiary,
                policyHolder,
                new ContractPaymentData(proposedPremium, proposedPaymentFrequency, this.currentTime, 0),
                coverageAmount,
                vehicleToInsure
        );

        this.chargePremiumOnContract(singleVehicleContract);

        this.contracts.add(singleVehicleContract);

        policyHolder.addContract(singleVehicleContract);

        return singleVehicleContract;
    }

    public TravelContract insurePersons(String contractNumber,
                                        Person policyHolder,
                                        int proposedPremium,
                                        PremiumPaymentFrequency proposedPaymentFrequency,
                                        Set<Person> personsToInsure){
        //validations
        //unique contract number check
        validateContractNumber(contractNumber);

        if (policyHolder == null) {
            throw new IllegalArgumentException("Policy holder cannot be null");
        }

        if (proposedPremium <= 0) {
            throw new IllegalArgumentException("Premium must be positive");
        }

        if (proposedPaymentFrequency == null) {
            throw new IllegalArgumentException("Premium frequency cannot be null");
        }

        if (personsToInsure == null || personsToInsure.isEmpty()) {
            throw new IllegalArgumentException("Insured persons set cannot be null or empty");
        }

        //amount paid by the policyholder must be greater than or equal to five times the number of insured persons.
        // Calculate annual premium based on payment frequency
        int annualPremium = (12 / proposedPaymentFrequency.getValueInMonths()) * proposedPremium;

        // Check if annual premium is at least 5 times the number of insured persons
        if (annualPremium < 5 * personsToInsure.size()) {
            throw new IllegalArgumentException("Annual premium must be at least five times the number of insured persons");
        }

        int coverageAmount = personsToInsure.size() * 10;

        TravelContract travelContract = new TravelContract(
                contractNumber,
                this,
                policyHolder,
                new ContractPaymentData(proposedPremium, proposedPaymentFrequency, this.currentTime, 0),
                coverageAmount,
                personsToInsure
        );

        this.chargePremiumOnContract(travelContract);

        this.contracts.add(travelContract);

        policyHolder.addContract(travelContract);

        return travelContract;
    }

    public MasterVehicleContract createMasterVehicleContract(String contractNumber,
                                                             Person beneficiary,
                                                             Person policyHolder){
        //validations
        //unique contract number check
        validateContractNumber(contractNumber);

        MasterVehicleContract masterVehicleContract = new MasterVehicleContract(
                contractNumber,
                this,
                beneficiary,
                policyHolder
        );

        this.contracts.add(masterVehicleContract);

        policyHolder.addContract(masterVehicleContract);

        return masterVehicleContract;
    }

    public void moveSingleVehicleContractToMasterVehicleContract(MasterVehicleContract masterVehicleContract, SingleVehicleContract singleVehicleContract ){
        //validation
        //should be not null
        if (masterVehicleContract == null){
            throw new IllegalArgumentException("Master Vehicle Contract must not be null");
        }
        if (singleVehicleContract == null){
            throw new IllegalArgumentException("Single Vehicle Contract must not be null.");
        }

        //should be active
        if(!masterVehicleContract.isActive()){
            throw new InvalidContractException("Master Vehicle Contract must be an active contract");
        }
        if(!singleVehicleContract.isActive()){
            throw new InvalidContractException("Single Vehicle Contract must be an active contract");
        }

        //must be insured by the insurer to which we are requesting the transfer
        if(!(masterVehicleContract.getInsurer().equals(this))){
            throw new InvalidContractException("Master Vehicle Contract must be insured by Insurance Company you are asking for transfer.");
        }
        if(!(singleVehicleContract.getInsurer().equals(this))){
            throw new InvalidContractException("Single Vehicle Contract must be insured by Insurance Company you are asking for transfer.");
        }

        //must have the same policyholder
        if(!(masterVehicleContract.getPolicyHolder().equals(singleVehicleContract.getPolicyHolder()))){
            throw new InvalidContractException("The policyholder of the single vehicle policy you are applying to transfer to a master vehicle policy should be the same.");
        }

        this.contracts.remove(singleVehicleContract);

        masterVehicleContract.getPolicyHolder().getContracts().remove(singleVehicleContract);

        masterVehicleContract.getChildContracts().add(singleVehicleContract);
    }

    public void chargePremiumsOnContracts(){
        for (AbstractContract contract : contracts){
            if(contract.isActive()){
                contract.updateBalance();
            }
        }
    }

    public void chargePremiumOnContract(MasterVehicleContract contract){
        //no validation needed
        for(SingleVehicleContract childContract : contract.getChildContracts()){
            chargePremiumOnContract(childContract);
        }
    }

    public void chargePremiumOnContract(AbstractContract contract){
        //no validation needed
        while(contract.getContractPaymentData().getNextPaymentTime().isBefore(this.currentTime) ||
                contract.getContractPaymentData().getNextPaymentTime().isEqual(this.currentTime)){
            contract.getContractPaymentData().setOutstandingBalance(contract.getContractPaymentData().getOutstandingBalance() + contract.getContractPaymentData().getPremium());
            contract.getContractPaymentData().updateNextPaymentTime();
        }
    }

    public void processClaim(TravelContract travelContract, Set<Person> affectedPersons){
        if (travelContract == null){
            throw new IllegalArgumentException("Travel Contract must not be null.");
        }
        if (affectedPersons == null || affectedPersons.isEmpty()){
            throw new IllegalArgumentException("List of affected persons must not be null or empty.");
        }
        if(!travelContract.getInsuredPersons().containsAll(affectedPersons)){
            throw new IllegalArgumentException("All affected persons must not be must be insured by Travel Contract.");
        }
        if(!travelContract.isActive()){
            throw new InvalidContractException("Travel Contract must be active contract.");
        }

        for (Person person : affectedPersons) {
            person.payout(travelContract.getCoverageAmount() / affectedPersons.size());
        }

        travelContract.setInactive();
    }

    public void processClaim(SingleVehicleContract singleVehicleContract, int expectedDamages){
        if (singleVehicleContract == null){
            throw new IllegalArgumentException("Single Vehicle Contract must not be null.");
        }
        if (expectedDamages <= 0){
            throw new IllegalArgumentException("Expected damages must be positive number.");
        }
        if(!singleVehicleContract.isActive()){
            throw new InvalidContractException("Single Vehicle Contract must be an active contract.");
        }

        if(singleVehicleContract.getBeneficiary() != null){
            singleVehicleContract.getBeneficiary().payout(singleVehicleContract.getCoverageAmount());
        } else {
            singleVehicleContract.getPolicyHolder().payout(singleVehicleContract.getCoverageAmount());
        }

        if(expectedDamages >= (0.7 * singleVehicleContract.getInsuredVehicle().getOriginalValue())){
            singleVehicleContract.setInactive();
        }
    }
}
