package myTests;

import company.InsuranceCompany;
import contracts.*;
import objects.Person;
import objects.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payment.PremiumPaymentFrequency;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InsuranceCompanyTest {

    private InsuranceCompany insurer;
    private Person policyHolder;
    private Person beneficiary;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        insurer = new InsuranceCompany(LocalDateTime.now());
        policyHolder = new Person("12345678"); // Valid ICO
        beneficiary = new Person("12345679"); // Another valid ICO
        vehicle = new Vehicle("ABC1234", 20000);
    }

    @Test
    void testCreateInsuranceCompanyNullTime() {
        assertThrows(IllegalArgumentException.class, () -> new InsuranceCompany(null));
    }

    @Test
    void testInsureVehicleSuccess() {
        SingleVehicleContract contract = insurer.insureVehicle(
                "V001", beneficiary, policyHolder, 500, PremiumPaymentFrequency.ANNUAL, vehicle
        );

        assertNotNull(contract);
        assertTrue(insurer.getContracts().contains(contract));
        assertEquals(10000, contract.getCoverageAmount()); // 20000 / 2
    }

    @Test
    void testInsureVehicleDuplicateContractNumber() {
        insurer.insureVehicle("V001", beneficiary, policyHolder, 500, PremiumPaymentFrequency.ANNUAL, vehicle);
        assertThrows(IllegalArgumentException.class, () ->
                insurer.insureVehicle("V001", beneficiary, policyHolder, 500, PremiumPaymentFrequency.ANNUAL, vehicle)
        );
    }

    @Test
    void testInsureVehiclePremiumTooLow() {
        // 2% of 20000 = 400, takže 300 je málo
        assertThrows(IllegalArgumentException.class, () ->
                insurer.insureVehicle("V002", beneficiary, policyHolder, 300, PremiumPaymentFrequency.ANNUAL, vehicle)
        );
    }

    @Test
    void testInsurePersonsSuccess() {
        Set<Person> insuredPersons = new HashSet<>();
        insuredPersons.add(new Person("0456281232")); // Fyzická osoba
        insuredPersons.add(new Person("9956119998")); // Fyzická osoba

        TravelContract travelContract = insurer.insurePersons(
                "T001", policyHolder, 20, PremiumPaymentFrequency.MONTHLY, insuredPersons
        );

        assertNotNull(travelContract);
        assertTrue(insurer.getContracts().contains(travelContract));
        assertEquals(20, travelContract.getContractPaymentData().getPremium());
    }


    @Test
    void testInsurePersonsInvalidPremium() {
        Set<Person> insuredPersons = new HashSet<>();
        insuredPersons.add(new Person("12345612"));

        // 5*1 = 5 => premium musí byť aspoň 5
        assertThrows(IllegalArgumentException.class, () ->
                insurer.insurePersons("T002", policyHolder, 4, PremiumPaymentFrequency.MONTHLY, insuredPersons)
        );
    }

    @Test
    void testCreateMasterVehicleContractSuccess() {
        MasterVehicleContract masterContract = insurer.createMasterVehicleContract(
                "M001", beneficiary, policyHolder
        );

        assertNotNull(masterContract);
        assertTrue(insurer.getContracts().contains(masterContract));
    }

    @Test
    void testMoveSingleVehicleContractToMasterVehicleContractSuccess() {
        SingleVehicleContract single = insurer.insureVehicle(
                "SVC001", beneficiary, policyHolder, 500, PremiumPaymentFrequency.ANNUAL, vehicle
        );
        MasterVehicleContract master = insurer.createMasterVehicleContract(
                "MVC001", beneficiary, policyHolder
        );

        insurer.moveSingleVehicleContractToMasterVehicleContract(master, single);

        assertTrue(master.getChildContracts().contains(single));
        assertFalse(insurer.getContracts().contains(single));
    }

    @Test
    void testMoveSingleVehicleContractInvalidInsurer() {
        InsuranceCompany otherCompany = new InsuranceCompany(LocalDateTime.now());
        SingleVehicleContract single = otherCompany.insureVehicle(
                "SVC002", beneficiary, policyHolder, 500, PremiumPaymentFrequency.ANNUAL, vehicle
        );
        MasterVehicleContract master = insurer.createMasterVehicleContract(
                "MVC002", beneficiary, policyHolder
        );

        assertThrows(InvalidContractException.class, () ->
                insurer.moveSingleVehicleContractToMasterVehicleContract(master, single)
        );
    }

    @Test
    void testChargePremiumsOnContracts() {
        SingleVehicleContract single = insurer.insureVehicle(
                "SVC003", beneficiary, policyHolder, 500, PremiumPaymentFrequency.ANNUAL, vehicle
        );

        insurer.chargePremiumsOnContracts();

        assertTrue(single.getContractPaymentData().getOutstandingBalance() > 0);
    }

    @Test
    void testProcessTravelClaim() {
        Set<Person> insuredPersons = new HashSet<>();
        Person p1 = new Person("0456281232");
        Person p2 = new Person("9956119998");


        insuredPersons.add(p1);
        insuredPersons.add(p2);

        TravelContract travelContract = insurer.insurePersons(
                "TR001", policyHolder, 20, PremiumPaymentFrequency.MONTHLY, insuredPersons
        );

        insurer.processClaim(travelContract, Set.of(p1, p2));
        // Tu by si mohol ešte overovať, že payout bol zavolaný, ak by Person.payout() bolo sledovateľné
    }

    @Test
    void testProcessVehicleClaimDeactivateOnBigDamage() {
        SingleVehicleContract single = insurer.insureVehicle(
                "SVC004", beneficiary, policyHolder, 500, PremiumPaymentFrequency.ANNUAL, vehicle
        );

        insurer.processClaim(single, 15000); // 15000 > 0.7 * 20000

        assertFalse(single.isActive());
    }
}
