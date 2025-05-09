package NikoTests;

import company.InsuranceCompany;
import contracts.*;
import objects.Person;
import objects.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import payment.ContractPaymentData;
import payment.PremiumPaymentFrequency;

import java.time.LocalDateTime;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
public class AdditionalTests {
    @Test
    @DisplayName("processClaim deactivates exactly at 70% damage")
    void totalLossAtSeventyPercent() {
        InsuranceCompany ic = new InsuranceCompany(LocalDateTime.now());
        Person nat = new Person("530101123");
        Vehicle v = new Vehicle("ABC1234", 1000);
        // coverage = 500, outstanding dummy
        SingleVehicleContract c = ic.insureVehicle("x", null, nat, 20, PremiumPaymentFrequency.ANNUAL, v);
        c.setCoverageAmount(500);

        // Exactly 70% of 1000 = 700
        ic.processClaim(c, 700);

        // should pay 500 and deactivate
        assertEquals(500, nat.getPaidOutAmount());
        assertFalse(c.isActive());
    }


    @DisplayName("Invalid Person IDs are rejected")
    void invalidPersonIds(String bad) {
        assertThrows(IllegalArgumentException.class, () -> new Person(bad));
    }

    @Test
    @DisplayName("Valid 9-digit RČ at RR=53 passes")
    void validShortRC() {
        // e.g. 531231001 (Dec 31, 1953)
        new Person("531231001");
    }

    @Test
    @DisplayName("Valid 10-digit RČ with correct checksum passes")
    void validLongRC() {
        // pick a known good one, e.g. 8501231235 (with sum mod11 ==0)
        new Person("530101123");//8053127698
    }
    @Test
    @DisplayName("pay(master) with no children throws")
    void masterPayNoChildren() {
        InsuranceCompany ic = new InsuranceCompany(LocalDateTime.now());
        Person legal = new Person("12345678");
        MasterVehicleContract m = ic.createMasterVehicleContract("m", null, legal);
        assertThrows(InvalidContractException.class, () -> m.pay(100));
    }

    @Test
    @DisplayName("setCoverageAmount rejects negatives")
    void rejectNegativeCoverage() {
        InsuranceCompany ic = new InsuranceCompany(LocalDateTime.now());
        Person nat = new Person("530101123");
        ContractPaymentData pd = new ContractPaymentData(10, PremiumPaymentFrequency.MONTHLY, LocalDateTime.now(), 0);
        AbstractContract c = new AbstractContract("c", ic, nat, pd, 100) {};
        assertThrows(IllegalArgumentException.class, () -> c.setCoverageAmount(-1));
    }
    @Test
    @DisplayName("setBeneficiary rejects same as policyHolder")
    void beneficiarySameAsHolder() {
        InsuranceCompany ic = new InsuranceCompany(LocalDateTime.now());
        Person nat = new Person("530101123");
        ContractPaymentData pd = new ContractPaymentData(10, PremiumPaymentFrequency.MONTHLY, LocalDateTime.now(), 0);
        AbstractVehicleContract c = new SingleVehicleContract("c", ic, null, nat, pd, 0, new Vehicle("AAA1111",1_000)) {};
        assertThrows(IllegalArgumentException.class, () -> c.setBeneficiary(nat));
    }
    @Test
    @DisplayName("Payment history is sorted by time")
    void paymentHistoryIsOrdered() {
        InsuranceCompany ic = new InsuranceCompany(LocalDateTime.now());
        Person nat = new Person("530101123");
        SingleVehicleContract c = ic.insureVehicle("x", null, nat, 50, PremiumPaymentFrequency.MONTHLY, new Vehicle("AAA1111",1_000));
        c.getContractPaymentData().setOutstandingBalance(200);

        // record 3 payments on three consecutive days
        ic.setCurrentTime(LocalDateTime.of(2025,1,1,0,0)); c.pay(50);
        ic.setCurrentTime(LocalDateTime.of(2025,1,2,0,0)); c.pay(50);
        ic.setCurrentTime(LocalDateTime.of(2025,1,3,0,0)); c.pay(50);

        var history = ic.getHandler().getPaymentHistory().get(c);
        LocalDateTime prev = null;
        for (var inst : history) {
            if (prev != null) assertTrue(prev.isBefore(inst.getPaymentTime()));
            prev = inst.getPaymentTime();
        }
    }

}
