package myTests;



import company.InsuranceCompany;
import contracts.*;
import objects.Person;
import objects.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payment.PaymentHandler;
import payment.PremiumPaymentFrequency;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PaymentHandlerTest {

    private InsuranceCompany insurer;
    private PaymentHandler paymentHandler;
    private Person policyHolder;
    private Person beneficiary;
    private Vehicle vehicle;
    private SingleVehicleContract contract;
    private MasterVehicleContract masterContract;

    @BeforeEach
    void setUp() {
        insurer = new InsuranceCompany(LocalDateTime.now());
        paymentHandler = insurer.getHandler();
        policyHolder = new Person("12345678"); // valid ICO
        beneficiary = new Person("12345679");
        vehicle = new Vehicle("ABC1234", 10000);

        // Create single contract
        contract = insurer.insureVehicle(
                "C001", beneficiary, policyHolder, 100, PremiumPaymentFrequency.MONTHLY, vehicle
        );

        // Create master contract
        masterContract = insurer.createMasterVehicleContract(
                "MC001", beneficiary, policyHolder
        );
        // Move contract to master contract
        insurer.moveSingleVehicleContractToMasterVehicleContract(masterContract, contract);
    }

    @Test
    void testPaySimpleContractSuccess() {
        int oldBalance = contract.getContractPaymentData().getOutstandingBalance();
        paymentHandler.pay(contract, 50);
        assertEquals(oldBalance - 50, contract.getContractPaymentData().getOutstandingBalance());
        assertEquals(1, paymentHandler.getPaymentHistory().get(contract).size());
    }

    @Test
    void testPaySimpleContractInvalid() {
        contract.setInactive();
        assertThrows(InvalidContractException.class, () -> paymentHandler.pay(contract, 100));
    }

    @Test
    void testPaySimpleContractNull() {
        assertThrows(IllegalArgumentException.class, () -> paymentHandler.pay(null, 100));
    }

    @Test
    void testPaySimpleContractNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> paymentHandler.pay(contract, -10));
    }

    @Test
    void testPayMasterVehicleContractSuccess() {
        int oldBalance = contract.getContractPaymentData().getOutstandingBalance();
        paymentHandler.pay(masterContract, 50);

        // Malo by sa odpočítať zo singleVehicle contractu
        assertTrue(paymentHandler.getPaymentHistory().containsKey(masterContract));
        assertEquals(oldBalance - 50, contract.getContractPaymentData().getOutstandingBalance());
    }

    @Test
    void testPayMasterVehicleContractWithEmptyChildren() {
        MasterVehicleContract emptyMaster = insurer.createMasterVehicleContract("MC002", beneficiary, policyHolder);
        assertThrows(InvalidContractException.class, () -> paymentHandler.pay(emptyMaster, 100));
    }

    @Test
    void testPayMasterVehicleContractInactive() {
        masterContract.setInactive();
        assertThrows(InvalidContractException.class, () -> paymentHandler.pay(masterContract, 100));
    }

    @Test
    void testPaymentHistoryIsCorrectlyStored() {
        paymentHandler.pay(contract, 100);
        assertTrue(paymentHandler.getPaymentHistory().containsKey(contract));
        assertEquals(1, paymentHandler.getPaymentHistory().get(contract).size());
    }
}
