package dianaTests;

import company.InsuranceCompany;
import contracts.*;
import objects.Person;
import objects.Vehicle;
import payment.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MasterContractActivityTests {

    InsuranceCompany insurer;
    MasterVehicleContract master;
    SingleVehicleContract c1, c2;

    @BeforeEach
    void init() {
        insurer = new InsuranceCompany(LocalDateTime.of(2025, 5, 1, 0, 0));
        Person lp = new Person("12345678");

        master = insurer.createMasterVehicleContract("m1", null, lp);

        c1 = insurer.insureVehicle("s1", null, lp, 300,
                PremiumPaymentFrequency.ANNUAL,
                new Vehicle("AB12C34", 10_000));
        c2 = insurer.insureVehicle("s2", null, lp, 300,
                PremiumPaymentFrequency.ANNUAL,
                new Vehicle("AB12C35", 10_000));

        master.getChildContracts().addAll(Set.of(c1, c2));
    }

    @Test
    void activityReflectsChildren() {
        assertTrue(master.isActive());
        c1.setInactive();
        c2.setInactive();
        assertFalse(master.isActive());
    }

    @Test
    void setInactiveCascades() {
        master.setInactive();
        assertFalse(master.isActive());
        assertFalse(c1.isActive());
        assertFalse(c2.isActive());
    }
}
