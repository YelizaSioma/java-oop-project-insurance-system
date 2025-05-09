package payment;

import company.InsuranceCompany;
import contracts.AbstractContract;
import contracts.InvalidContractException;
import contracts.MasterVehicleContract;
import contracts.SingleVehicleContract;

import java.time.LocalDateTime;
import java.util.*;

public class PaymentHandler {
    //attributes
    private final Map<AbstractContract, Set<PaymentInstance>> paymentHistory;
    private final InsuranceCompany insurer;

    /**
     * @param insurer the company whose contracts are handled; must not be null
     * @throws IllegalArgumentException if insurer is null
     */
    //constructor
    public PaymentHandler(InsuranceCompany insurer){
        //validation
        validateInsurer(insurer);

        this.paymentHistory = new LinkedHashMap<>();
        this.insurer=insurer;
    }


    //___________Public methods___________
    public Map<AbstractContract, Set<PaymentInstance>> getPaymentHistory(){
        return paymentHistory;
    }


    public void pay(MasterVehicleContract contract, int amount){

        validateMasterVehicleContractAndAmount(contract,amount);

        int originalAmount = amount;

        //1st for
        for(SingleVehicleContract childContract : contract.getChildContracts().stream().filter(AbstractContract::isActive).toList()){
            //if there is an outstanding balance on the contract
            if(childContract.getContractPaymentData().getOutstandingBalance() > 0){
                //if amount still should be paid ????
                if(childContract.getContractPaymentData().getOutstandingBalance() <= amount){
                    amount -= childContract.getContractPaymentData().getOutstandingBalance();
                    childContract.getContractPaymentData().setOutstandingBalance(0);
                } else {
                    childContract.getContractPaymentData().setOutstandingBalance(childContract.getContractPaymentData().getOutstandingBalance() - amount);
                    amount = 0;
                    break;
                }
            }
        }

        //2nd for
        while (amount > 0) {
            boolean fundsUsed = false;

            for(SingleVehicleContract childContract : contract.getChildContracts().stream().filter(AbstractContract::isActive).toList()){
                //if amount still should be paid
                if(childContract.getContractPaymentData().getPremium() <= amount){
                    childContract.getContractPaymentData().setOutstandingBalance(childContract.getContractPaymentData().getOutstandingBalance() - childContract.getContractPaymentData().getPremium());
                    amount -= childContract.getContractPaymentData().getPremium();
                    fundsUsed = true;
                } else {
                    childContract.getContractPaymentData().setOutstandingBalance(childContract.getContractPaymentData().getOutstandingBalance() - amount);
                    amount = 0;
                    fundsUsed = true;
                    break;
                }
            }
            if (!fundsUsed) {
                break;
            }
        }

        PaymentInstance paymentInstance = new PaymentInstance(contract.getInsurer().getCurrentTime(), originalAmount - amount);

        if(getPaymentHistory().containsKey(contract)){
            //use directly paymentHistory instead of getter
            getPaymentHistory().get(contract).add(paymentInstance);
        } else {
            getPaymentHistory().put(contract, new TreeSet<>());
            getPaymentHistory().get(contract).add(paymentInstance);
        }
    }


    public void pay(AbstractContract contract, int amount){

        validateAbstractContractAndAmount(contract,amount);

        contract.getContractPaymentData().setOutstandingBalance(contract.getContractPaymentData().getOutstandingBalance()-amount);

        PaymentInstance paymentInstance = new PaymentInstance(contract.getInsurer().getCurrentTime(), amount);
        if(getPaymentHistory().containsKey(contract)){
            getPaymentHistory().get(contract).add(paymentInstance);
        } else {
            getPaymentHistory().put(contract, new TreeSet<>());
            getPaymentHistory().get(contract).add(paymentInstance);
        }
    }


    //___________Private helpers___________
    private void validateInsurer(InsuranceCompany insurer){
        if (insurer == null) {
            throw new IllegalArgumentException("Insurance Company cannot be null.");
        }
    }

    private void validateMasterVehicleContractAndAmount(MasterVehicleContract contract, int amount){
        if (contract == null) {
            throw new IllegalArgumentException("Master Vehicle Contract cannot be null in pay process.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to pay cannot be null in pay process.");
        }
        if (!contract.isActive()){
            throw new InvalidContractException("Contract must be active to make the payment.");
        }
        if(this.insurer.getHandler() != contract.getInsurer().getHandler()){
            throw new InvalidContractException("The handler must be set up to make the payment.");
        }
        if(contract.getChildContracts().isEmpty()){
            throw new InvalidContractException("List of child contract cannot be empty to make the payment.");
        }
    }

    private void validateAbstractContractAndAmount(AbstractContract contract, int amount){
        if (contract == null) {
            throw new IllegalArgumentException("Contract cannot be null in pay process.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to pay cannot be null in pay process.");
        }
        if (!contract.isActive()){
            throw new InvalidContractException("Contract must be active to make the payment.");
        }
        if(this.insurer.getHandler() != contract.getInsurer().getHandler()){
            throw new InvalidContractException("Handler differ from the expected one.");
        }
    }
}
