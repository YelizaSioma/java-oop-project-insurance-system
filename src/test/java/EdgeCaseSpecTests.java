

import company.InsuranceCompany;
import contracts.AbstractContract;
import contracts.MasterVehicleContract;
import contracts.SingleVehicleContract;
import contracts.TravelContract;
import contracts.InvalidContractException;
import objects.Person;
import objects.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import payment.ContractPaymentData;
import payment.PremiumPaymentFrequency;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Edge-Case Spec Tests")
public class EdgeCaseSpecTests {

    @Test
    @DisplayName("TravelContract rejects null/empty insured set or non-natural persons")
    void travelContractConstructorValidates() {
        InsuranceCompany ic = new InsuranceCompany(LocalDateTime.now());
        Person legal = new Person("12345678");
        ContractPaymentData pd = new ContractPaymentData(
                10, PremiumPaymentFrequency.ANNUAL,
                LocalDateTime.now(), 0
        );
        // null set
        assertThrows(IllegalArgumentException.class,
                () -> new TravelContract("t1", ic, legal, pd, 100, null));
        // empty set
        assertThrows(IllegalArgumentException.class,
                () -> new TravelContract("t2", ic, legal, pd, 100, Set.of()));
        // set containing a legal person
        assertThrows(IllegalArgumentException.class,
                () -> new TravelContract("t3", ic, legal, pd, 100, Set.of(legal)));
    }

    @Test
    @DisplayName("TravelContract.processClaim splits and deactivates")
    void travelContractProcessClaim() {
        LocalDateTime now = LocalDateTime.of(2025, 5, 1, 0, 0);
        InsuranceCompany ic = new InsuranceCompany(now);
        Person nat1 = new Person("530101123");
        Person nat2 = new Person("530202234");
        Set<Person> insured = Set.of(nat1, nat2);
        // correct usage of insurePersons
        ContractPaymentData pd = new ContractPaymentData(
                50, PremiumPaymentFrequency.SEMI_ANNUAL,
                now, 0
        );
        // directly construct TravelContract via constructor
        TravelContract tc = new TravelContract(
                "TP", ic, nat1,
                pd, 100, insured
        );

        // force coverage to a known number
        tc.setCoverageAmount(100);
        assertTrue(tc.isActive());

        // process claim for only one person
        ic.processClaim(tc, Set.of(nat1));

        // payout = 100, contract must now be inactive
        assertEquals(100, nat1.getPaidOutAmount());
        assertEquals(0,   nat2.getPaidOutAmount());
        assertFalse(tc.isActive());
    }

    @Test
    @DisplayName("Master.updateBalance cascades into children")
    void masterUpdateBalanceCascades() {
        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 0, 0);
        InsuranceCompany ic = new InsuranceCompany(now);
        Person legal = new Person("12345678");
        Vehicle v = new Vehicle("ABC1234", 10_000);

        // child due yesterday, premium 100
        ContractPaymentData pd = new ContractPaymentData(
                100, PremiumPaymentFrequency.ANNUAL,
                now.minusDays(1), 0
        );
        SingleVehicleContract child = new SingleVehicleContract(
                "X1", ic, null, legal, pd, 0, v
        );

        MasterVehicleContract master = ic.createMasterVehicleContract(
                "M1", null, legal
        );
        master.requestAdditionOfChildContract(child);

        // advance the company's clock to simulate due date passed
        ic.setCurrentTime(now);
        master.updateBalance();

        assertEquals(100,
                child.getContractPaymentData().getOutstandingBalance());
        assertEquals(now.minusDays(1).plusYears(1),
                child.getContractPaymentData().getNextPaymentTime());
    }

    @Test
    @DisplayName("InsuranceCompany.chargePremiumsOnContracts handles both contract types")
    void chargePremiumsOnContractsAllTypes() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 0, 0);
        InsuranceCompany ic = new InsuranceCompany(now);
        Person nat = new Person("530101123");

        SingleVehicleContract s = ic.insureVehicle(
                "S1", null, nat,
                100, PremiumPaymentFrequency.MONTHLY,
                new Vehicle("AAA1111", 12_000)
        );
        ContractPaymentData pd = new ContractPaymentData(
                10, PremiumPaymentFrequency.MONTHLY,
                now, 0
        );
        // create via factory so it's registered
        TravelContract t = ic.insurePersons(
                "T1",
                nat,
                10,
                PremiumPaymentFrequency.MONTHLY,
                Set.of(nat)
        );

        // advance the insurer's clock one day past due
        ic.setCurrentTime(now.plusDays(1));
        ic.chargePremiumsOnContracts();

        assertEquals(100,
                s.getContractPaymentData().getOutstandingBalance());
        assertEquals(10,
                t.getContractPaymentData().getOutstandingBalance());
    }

    @Test
    @DisplayName("Move single→master rejects wrong policyHolder or inactive")
    void moveSingleToMasterValidations() {
        LocalDateTime now = LocalDateTime.now();
        InsuranceCompany ic = new InsuranceCompany(now);
        Person legal      = new Person("12345678");
        Person otherLegal = new Person("87654321");
        Vehicle v = new Vehicle("ABC1234", 20_000);
        // use safe premium to pass 2% rule
        int safePremium = 1000;

        SingleVehicleContract single = ic.insureVehicle(
                "S1", null, legal,
                safePremium, PremiumPaymentFrequency.ANNUAL,
                v
        );
        SingleVehicleContract single2 = ic.insureVehicle(
                "S2", null, legal,
                safePremium, PremiumPaymentFrequency.ANNUAL,
                v
        );

        MasterVehicleContract m1 = ic.createMasterVehicleContract(
                "M1", null, legal
        );
        MasterVehicleContract m2 = ic.createMasterVehicleContract(
                "M2", null, otherLegal
        );

        // inactive single
        single.setInactive();
        assertThrows(InvalidContractException.class,
                () -> ic.moveSingleVehicleContractToMasterVehicleContract(m1,single));

        // wrong holder
        assertThrows(InvalidContractException.class,
                () -> ic.moveSingleVehicleContractToMasterVehicleContract( m2,single2));
    }

    @Test
    @DisplayName("AbstractContract.pay() dispatches to correct handler overload")
    void abstractContractPayDispatches() {
        LocalDateTime now = LocalDateTime.of(2025, 7, 1, 0, 0);
        InsuranceCompany ic = new InsuranceCompany(now);
        Person legal = new Person("12345678");

        int safePremium = 1000;
        SingleVehicleContract s = ic.insureVehicle(
                "P1", null, legal,
                safePremium, PremiumPaymentFrequency.ANNUAL,
                new Vehicle("AAA1111", 15_000)
        );
        s.getContractPaymentData().setOutstandingBalance(100);

        // contract.pay should call handler for single
        s.pay(50);
        assertEquals(50, s.getContractPaymentData().getOutstandingBalance());

        // master.pay should call handler for master
        MasterVehicleContract master = ic.createMasterVehicleContract(
                "PM", null, legal
        );
        master.requestAdditionOfChildContract(s);
        ic.getContracts().add(master);

        int before = s.getContractPaymentData().getOutstandingBalance();
        master.pay(20);
        assertTrue(s.getContractPaymentData().getOutstandingBalance() < before);
    }
}
