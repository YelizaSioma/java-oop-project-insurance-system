package myTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import company.InsuranceCompany;
import contracts.AbstractVehicleContract;
import objects.Person;
import payment.ContractPaymentData;
import payment.PremiumPaymentFrequency;

import java.time.LocalDateTime;

public class AbstractVehicleContractTests {

    private Person policyHolder;
    private Person beneficiary;
    private InsuranceCompany insurer;
    private ContractPaymentData paymentData;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        policyHolder = new Person("123456"); // 6-miestne IČO je podľa zadania validné
        beneficiary = new Person("12345678"); // 8-
        insurer = new InsuranceCompany(now);
        paymentData = new ContractPaymentData(100, PremiumPaymentFrequency.ANNUAL, now, 0);
    }

    @Test
    void testValidContractCreation() {
        MockVehicleContract contract = new MockVehicleContract(
                "VC001", insurer, policyHolder, paymentData, 1000, beneficiary);

        assertEquals("VC001", contract.getContractNumber());
        assertSame(insurer, contract.getInsurer());
        assertSame(policyHolder, contract.getPolicyHolder());
        assertSame(paymentData, contract.getContractPaymentData());
        assertEquals(1000, contract.getCoverageAmount());
        assertSame(beneficiary, contract.getBeneficiary());
        assertTrue(contract.isActive());
    }

    @Test
    void testNullBeneficiary() {
        // Null beneficiary je povolený
        MockVehicleContract contract = new MockVehicleContract(
                "VC001", insurer, policyHolder, paymentData, 1000, null);

        assertNull(contract.getBeneficiary());
    }

    @Test
    void testInvalidBeneficiary() {
        // Beneficiary je rovnaký ako policyHolder
        assertThrows(IllegalArgumentException.class,
                () -> new MockVehicleContract("VC001", insurer, policyHolder, paymentData, 1000, policyHolder));
    }

    // Pomocná implementácia abstraktnej triedy na účely testovania
    private static class MockVehicleContract extends AbstractVehicleContract {
        public MockVehicleContract(String contractNumber, InsuranceCompany insurer, Person policyHolder,
                                   ContractPaymentData contractPaymentData, int coverageAmount, Person beneficiary) {
            super(contractNumber, insurer, beneficiary, policyHolder, contractPaymentData, coverageAmount);
        }
    }
}