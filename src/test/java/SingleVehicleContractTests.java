package myTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import company.InsuranceCompany;
import contracts.SingleVehicleContract;
import objects.Person;
import objects.Vehicle;
import payment.ContractPaymentData;
import payment.PremiumPaymentFrequency;

import java.time.LocalDateTime;

public class SingleVehicleContractTests {

    private Person policyHolder;
    private Person beneficiary;
    private InsuranceCompany insurer;
    private ContractPaymentData paymentData;
    private Vehicle vehicle;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        policyHolder = new Person("0012232539");
        beneficiary = new Person("0012232539");
        insurer = new InsuranceCompany(now);
        paymentData = new ContractPaymentData(100, PremiumPaymentFrequency.ANNUAL, now, 0);
        vehicle = new Vehicle("BA123CD", 10000);
    }

    @Test
    void testValidContractCreation() {
        SingleVehicleContract contract = new SingleVehicleContract(
                "SVC001", insurer,beneficiary, policyHolder, paymentData, 1000, vehicle);

        assertEquals("SVC001", contract.getContractNumber());
        assertSame(insurer, contract.getInsurer());
        assertSame(policyHolder, contract.getPolicyHolder());
        assertSame(paymentData, contract.getContractPaymentData());
        assertEquals(1000, contract.getCoverageAmount());
        assertSame(beneficiary, contract.getBeneficiary());
        assertSame(vehicle, contract.getInsuredVehicle());
        assertTrue(contract.isActive());
    }

    @Test
    void testNullBeneficiary() {
        // Null beneficiary je povolený
        SingleVehicleContract contract = new SingleVehicleContract(
                "SVC001", insurer,null, policyHolder, paymentData, 1000, vehicle);

        assertNull(contract.getBeneficiary());
        assertSame(vehicle, contract.getInsuredVehicle());
    }

    @Test
    void testInvalidVehicle() {
        // Null vehicle
        assertThrows(IllegalArgumentException.class,
                () -> new SingleVehicleContract("SVC001", insurer,beneficiary, policyHolder, paymentData, 1000,  null));
    }

    @Test
    void testInvalidPaymentData() {
        // Null contractPaymentData
        assertThrows(IllegalArgumentException.class,
                () -> new SingleVehicleContract("SVC001", insurer,beneficiary, policyHolder, null, 1000,  vehicle));
    }
}
