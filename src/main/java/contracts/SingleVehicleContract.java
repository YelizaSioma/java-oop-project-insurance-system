package contracts;

import company.InsuranceCompany;
import objects.Person;
import objects.Vehicle;
import payment.ContractPaymentData;

public class SingleVehicleContract extends AbstractVehicleContract{
    private final Vehicle insuredVehicle;

    /**
     * @param contractPaymentData non-null
     * @param vehicleToInsure     non-null
     *
     * @throws IllegalArgumentException if contractPaymentData is null or vehicleToInsure is null
     */
    //constructor
    public SingleVehicleContract(String contractNumber,
                                 InsuranceCompany insurer,
                                 Person beneficiary,
                                 Person policyHolder,
                                 ContractPaymentData contractPaymentData,
                                 int coverageAmount,
                                 Vehicle vehicleToInsure){
        //super-class constructor
        super(contractNumber, insurer, beneficiary, policyHolder, contractPaymentData, coverageAmount);

        //validation
        validateSingleVehicleContractParams(contractPaymentData, vehicleToInsure);

        this.insuredVehicle=vehicleToInsure;
    }

    //___________Public methods___________
    public Vehicle getInsuredVehicle(){
        return insuredVehicle;
    }


    //___________Private helpers___________
    /**
     * Validates params for a SingleVehicleContract.
     *
     * @throws IllegalArgumentException if contractPaymentData or vehicleToInsure is null
     */
    private void validateSingleVehicleContractParams(ContractPaymentData contractPaymentData, Vehicle vehicleToInsure) {
        if (contractPaymentData == null) {
            throw new IllegalArgumentException("Contract payment data cannot be null");
        }
        if (vehicleToInsure == null){
            throw new IllegalArgumentException("Vehicle to insure cannot be null");
        }
    }
}
