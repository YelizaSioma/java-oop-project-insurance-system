package myTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import company.InsuranceCompany;
import contracts.AbstractContract;
import objects.Person;
import payment.ContractPaymentData;
import payment.PremiumPaymentFrequency;

import java.time.LocalDateTime;

public class AbstractContractTests {

    private Person policyHolder;
    private InsuranceCompany insurer;
    private ContractPaymentData paymentData;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        policyHolder = new Person("12345678");
        insurer = new InsuranceCompany(now);
        paymentData = new ContractPaymentData(100, PremiumPaymentFrequency.ANNUAL, now, 0);
    }

    @Test
    void testValidContractCreation() {
        MockContract contract = new MockContract("C001", insurer, policyHolder, paymentData, 500);

        assertEquals("C001", contract.getContractNumber());
        assertSame(insurer, contract.getInsurer());
        assertSame(policyHolder, contract.getPolicyHolder());
        assertSame(paymentData, contract.getContractPaymentData());
        assertEquals(500, contract.getCoverageAmount());
        assertTrue(contract.isActive());
    }

    @Test
    void testInvalidContractNumber() {
        // Null hodnota
        assertThrows(IllegalArgumentException.class,
                () -> new MockContract(null, insurer, policyHolder, paymentData, 500));

        // Prázdny reťazec
        assertThrows(IllegalArgumentException.class,
                () -> new MockContract("", insurer, policyHolder, paymentData, 500));
    }

    @Test
    void testInvalidInsurer() {
        // Null hodnota
        assertThrows(IllegalArgumentException.class,
                () -> new MockContract("C001", null, policyHolder, paymentData, 500));
    }

    @Test
    void testInvalidPolicyHolder() {
        // Null hodnota
        assertThrows(IllegalArgumentException.class,
                () -> new MockContract("C001", insurer, null, paymentData, 500));
    }

    @Test
    void testInvalidCoverageAmount() {
        // Záporná hodnota
        assertThrows(IllegalArgumentException.class,
                () -> new MockContract("C001", insurer, policyHolder, paymentData, -500));

        // Test set metódy
        MockContract contract = new MockContract("C001", insurer, policyHolder, paymentData, 500);
        assertThrows(IllegalArgumentException.class, () -> contract.setCoverageAmount(-10));
    }

    @Test
    void testSetActive() {
        MockContract contract = new MockContract("C001", insurer, policyHolder, paymentData, 500);
        assertTrue(contract.isActive());

        contract.setInactive();
        assertFalse(contract.isActive());
    }

    // Pomocná implementácia abstraktnej triedy na účely testovania
    private static class MockContract extends AbstractContract {
        public MockContract(String contractNumber, InsuranceCompany insurer, Person policyHolder,
                            ContractPaymentData contractPaymentData, int coverageAmount) {
            super(contractNumber, insurer, policyHolder, contractPaymentData, coverageAmount);
        }
    }
}
