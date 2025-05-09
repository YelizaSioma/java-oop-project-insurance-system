package NikoTests;

import company.InsuranceCompany;
import contracts.MasterVehicleContract;
import contracts.SingleVehicleContract;
import objects.Person;
import objects.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import payment.ContractPaymentData;
import payment.PremiumPaymentFrequency;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MasterContract pay() matches spec pseudocode (Table 2)")
public class MasterContractDistributionSpec {
    InsuranceCompany ic;
    Person legal;
    SingleVehicleContract c1, c2, c3, c4;
    MasterVehicleContract master;

    @BeforeEach
    void setup() {
        ic = new InsuranceCompany(LocalDateTime.of(2025, 1, 1, 0, 0));
        legal = new Person("12345678");

        Vehicle v1 = new Vehicle("AAA1111", 15_000); // contract1
        Vehicle v2 = new Vehicle("BBB2222", 22_000); // contract2
        Vehicle v3 = new Vehicle("CCC3333", 8_000);  // contract3
        Vehicle v4 = new Vehicle("DDD4444", 40_000); // contract4

        // Create all with a safe monthly premium so yearly ≥ 2% of any vehicle
        int safePremium = 1200;                      // 1200 * 12 = 14 400 per year
        PremiumPaymentFrequency freq = PremiumPaymentFrequency.MONTHLY;

        c1 = ic.insureVehicle("c1", null, legal, safePremium, freq, v1);
        c2 = ic.insureVehicle("c2", null, legal, safePremium, freq, v2);
        c3 = ic.insureVehicle("c3", null, legal, safePremium, freq, v3);
        c4 = ic.insureVehicle("c4", null, legal, safePremium, freq, v4);

        // Now override premiums & outstanding balances to match Table 2
        c1.getContractPaymentData().setPremium(30);
        c1.getContractPaymentData().setOutstandingBalance(30);

        c2.getContractPaymentData().setPremium(50);
        c2.getContractPaymentData().setOutstandingBalance(50);

        c3.getContractPaymentData().setPremium(75);
        c3.getContractPaymentData().setOutstandingBalance(100);

        c4.getContractPaymentData().setPremium(20);
        c4.getContractPaymentData().setOutstandingBalance(0);

        // Build master
        master = ic.createMasterVehicleContract("m1", null, legal);
        master.requestAdditionOfChildContract(c1);
        master.requestAdditionOfChildContract(c2);
        master.requestAdditionOfChildContract(c3);
        master.requestAdditionOfChildContract(c4);
        c4.setInactive();  // contract4 is inactive per spec
        // Make sure the master is tracked at top level too
        ic.getContracts().add(master);
    }

    @Test
    @DisplayName("Final balances after pay(master, 400) should be exactly as Table 2")
    void finalDistributionMatchesTable2() {
        ic.getHandler().pay(master, 400);

        assertEquals(-60, c1.getContractPaymentData().getOutstandingBalance(), "c1 should end at -60");
        assertEquals(-85, c2.getContractPaymentData().getOutstandingBalance(), "c2 should end at -85");
        assertEquals(-75, c3.getContractPaymentData().getOutstandingBalance(), "c3 should end at -75");
        assertEquals(0,   c4.getContractPaymentData().getOutstandingBalance(),  "c4 stays at 0");
    }
}
