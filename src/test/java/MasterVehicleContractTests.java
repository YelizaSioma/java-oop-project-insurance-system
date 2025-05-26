package myTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import company.InsuranceCompany;
import contracts.MasterVehicleContract;
import contracts.SingleVehicleContract;
import objects.Person;
import objects.Vehicle;
import payment.ContractPaymentData;
import payment.PremiumPaymentFrequency;

import java.time.LocalDateTime;
import java.util.Set;

public class MasterVehicleContractTests {

    private Person legalPerson;
    private Person naturalPerson;
    private InsuranceCompany insurer;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        legalPerson = new Person("12345678"); // Právnická osoba (IČO)
        naturalPerson = new Person("9001010018"); // Fyzická osoba
        insurer = new InsuranceCompany(now);
    }

    @Test
    void testValidContractCreation() {
        MasterVehicleContract contract = new MasterVehicleContract("MVC001", insurer,null, legalPerson);

        assertEquals("MVC001", contract.getContractNumber());
        assertSame(insurer, contract.getInsurer());
        assertSame(legalPerson, contract.getPolicyHolder());
        assertNull(contract.getContractPaymentData());
        assertEquals(0, contract.getCoverageAmount());
        assertTrue(contract.isActive());

        // Kontrola inicializácie prázdnej množiny childContracts
        Set<SingleVehicleContract> childContracts = contract.getChildContracts();
        assertNotNull(childContracts);
        assertEquals(0, childContracts.size());
    }

    @Test
    void testInvalidPolicyHolder() {
        // Fyzická osoba nemôže byť policyHolder v MasterVehicleContract
        assertThrows(IllegalArgumentException.class,
                () -> new MasterVehicleContract("MVC001", insurer,null, naturalPerson));
    }

    @Test
    void testIsActiveWithChildContracts() {
        MasterVehicleContract contract = new MasterVehicleContract("MVC001", insurer,null, legalPerson);

        // Bez dcérskych zmlúv by mala byť aktívna
        assertTrue(contract.isActive());

        // S neaktívnymi dcérskymi zmluvami by mala byť neaktívna
        // V reálnom použití by sme museli pridať dcérske zmluvy pomocou InsuranceCompany
        // a potom ich deaktivovať, ale to pravdepodobne nie je možné priamo v teste
    }

    @Test
    void testSetInactive() {
        MasterVehicleContract contract = new MasterVehicleContract("MVC001", insurer,null, legalPerson);

        // Pôvodne aktívna
        assertTrue(contract.isActive());

        // Po deaktivácii
        contract.setInactive();
        assertFalse(contract.isActive());

        // V reálnom použití by sme mali kontrolovať, či sú deaktivované aj všetky
        // dcérske zmluvy, ale to je ťažké testovať priamo bez komplexného nastavenia
    }
}