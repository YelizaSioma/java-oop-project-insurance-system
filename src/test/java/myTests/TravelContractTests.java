package myTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import company.InsuranceCompany;
import contracts.TravelContract;
import objects.Person;
import objects.LegalForm;
import payment.ContractPaymentData;
import payment.PremiumPaymentFrequency;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class TravelContractTests {

    private Person naturalPerson1;
    private Person naturalPerson2;
    private Person legalPerson;
    private InsuranceCompany insurer;
    private ContractPaymentData paymentData;
    private LocalDateTime now;
    private Set<Person> insuredPersons;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        naturalPerson1 = new Person("0012232539");
        naturalPerson2 = new Person("0012232539");
        legalPerson = new Person("12345678");
        insurer = new InsuranceCompany(now);
        paymentData = new ContractPaymentData(100, PremiumPaymentFrequency.ANNUAL, now, 0);

        insuredPersons = new HashSet<>();
        insuredPersons.add(naturalPerson1);
        insuredPersons.add(naturalPerson2);
    }

    @Test
    void testValidTravelContractCreation() {
        TravelContract contract = new TravelContract("TC001", insurer, naturalPerson1, paymentData, 1000, insuredPersons);

        assertEquals("TC001", contract.getContractNumber());
        assertSame(insurer, contract.getInsurer());
        assertSame(naturalPerson1, contract.getPolicyHolder());
        assertSame(paymentData, contract.getContractPaymentData());
        assertEquals(1000, contract.getCoverageAmount());
        assertTrue(contract.isActive());

        // Test, že poistené osoby boli správne nastavené
        Set<Person> retrievedPersons = contract.getInsuredPersons();
        assertEquals(2, retrievedPersons.size());
        assertTrue(retrievedPersons.contains(naturalPerson1));
        assertTrue(retrievedPersons.contains(naturalPerson2));
    }

    @Test
    void testLegalPersonAsPolicyHolder() {
        // Právnická osoba ako poistník je povolená
        TravelContract contract = new TravelContract("TC001", insurer, legalPerson, paymentData, 1000, insuredPersons);
        assertSame(legalPerson, contract.getPolicyHolder());
    }

    @Test
    void testInvalidInsuredPersons() {
        // Null hodnota
        assertThrows(IllegalArgumentException.class,
                () -> new TravelContract("TC001", insurer, naturalPerson1, paymentData, 1000, null));

        // Prázdna množina
        Set<Person> emptySet = new HashSet<>();
        assertThrows(IllegalArgumentException.class,
                () -> new TravelContract("TC001", insurer, naturalPerson1, paymentData, 1000, emptySet));

        // Množina obsahujúca právnickú osobu
        Set<Person> withLegalPerson = new HashSet<>();
        withLegalPerson.add(naturalPerson1);
        withLegalPerson.add(legalPerson);
        assertThrows(IllegalArgumentException.class,
                () -> new TravelContract("TC001", insurer, naturalPerson1, paymentData, 1000, withLegalPerson));
    }

    @Test
    void testInvalidPaymentData() {
        // Null hodnota pre contractPaymentData
        assertThrows(IllegalArgumentException.class,
                () -> new TravelContract("TC001", insurer, naturalPerson1, null, 1000, insuredPersons));
    }
}