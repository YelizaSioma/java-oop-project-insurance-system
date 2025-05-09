package NikoTests;

import company.InsuranceCompany;
import contracts.AbstractContract;
import contracts.MasterVehicleContract;
import contracts.SingleVehicleContract;
import contracts.InvalidContractException;
import objects.Person;
import objects.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import payment.ContractPaymentData;
import payment.PaymentInstance;
import payment.PremiumPaymentFrequency;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PaymentHandler & InsuranceCompany Flows")
public class PaymentAndCompanyTests {
    InsuranceCompany ic;
    Person legal;
    Person natural;
    Vehicle v1, v2;

    @BeforeEach
    void setup() {
        ic = new InsuranceCompany(LocalDateTime.of(2025,4,1,0,0));
        legal = new Person("12345678");      // legal
        natural = new Person("530101123");   // natural
        v1 = new Vehicle("ABC1234", 20_000);
        v2 = new Vehicle("XYZ9999", 10_000);
    }

    @Test
    @DisplayName("Single‐contract payment reduces balance and records history")
    void singleContractPay() {
        // insure v1: use premium 400 semi‐annual => annualized 400*12/6 = 800 >= 2%*20000=400
        SingleVehicleContract c = ic.insureVehicle("C1", null, legal, 400, PremiumPaymentFrequency.SEMI_ANNUAL, v1);
        // after first charge, outstandingBalance == premium (400)
        assertEquals(400, c.getContractPaymentData().getOutstandingBalance());

        // pay 150
        ic.getHandler().pay(c, 150);
        assertEquals(250, c.getContractPaymentData().getOutstandingBalance());

        // history
        Map<AbstractContract, Set<PaymentInstance>> records = ic.getHandler().getPaymentHistory();
        assertTrue(records.containsKey(c));
        List<PaymentInstance> history = List.copyOf(records.get(c));
        assertEquals(1, history.size());
        PaymentInstance inst = history.getFirst();
        assertEquals(150, inst.getPaymentAmount());
        assertTrue(inst.getPaymentTime().isEqual(ic.getCurrentTime()));
    }

    @Test
    @DisplayName("Master‐contract multi‐phase payment distributes correctly")
    void masterContractPayDistribution() {
        // create four single contracts under same legal holder
        SingleVehicleContract c1 = ic.insureVehicle("c1", null, legal, 1500, PremiumPaymentFrequency.ANNUAL,     v1);
        SingleVehicleContract c2 = ic.insureVehicle("c2", null, legal, 184,  PremiumPaymentFrequency.MONTHLY,     v2);
        SingleVehicleContract c3 = ic.insureVehicle("c3", null, legal, 400,  PremiumPaymentFrequency.SEMI_ANNUAL, v1);
        SingleVehicleContract c4 = ic.insureVehicle("c4", null, legal, 1000, PremiumPaymentFrequency.QUARTERLY,  v2);

        // manually set premiums and outstanding balances for distribution
        c1.getContractPaymentData().setPremium(30);  c1.getContractPaymentData().setOutstandingBalance(30);
        c2.getContractPaymentData().setPremium(50);  c2.getContractPaymentData().setOutstandingBalance(50);
        c3.getContractPaymentData().setPremium(75);  c3.getContractPaymentData().setOutstandingBalance(100);
        c4.getContractPaymentData().setPremium(20);  c4.getContractPaymentData().setOutstandingBalance(0);
        // deactivate the fourth so it's ignored

        // build master
        MasterVehicleContract m = ic.createMasterVehicleContract("M1", null, legal);
        m.requestAdditionOfChildContract(c1);
        m.requestAdditionOfChildContract(c2);
        m.requestAdditionOfChildContract(c3);
        m.requestAdditionOfChildContract(c4);
        c4.setInactive();
        // ensure it's top‐level
        ic.getContracts().add(m);

        // pay 400
        ic.getHandler().pay(m, 400);

        assertEquals(-60, c1.getContractPaymentData().getOutstandingBalance());
        assertEquals(-85, c2.getContractPaymentData().getOutstandingBalance());
        assertEquals(-75, c3.getContractPaymentData().getOutstandingBalance());
        assertEquals(0,   c4.getContractPaymentData().getOutstandingBalance());

        // single history entry on master
        Map<AbstractContract, Set<PaymentInstance>> hist = ic.getHandler().getPaymentHistory();
        assertTrue(hist.containsKey(m));
        assertEquals(1, hist.get(m).size());
    }

    @Test
    @DisplayName("Master holder must be legal person")
    void masterHolderMustBeLegal() {
        assertThrows(IllegalArgumentException.class,
                () -> ic.createMasterVehicleContract("M2", null, natural));
    }
    @Test
    @DisplayName("Balance updates: semi-annual adds 2× premium over a year")
    void semiAnnualChargeAddsTwoPayments() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 31, 0, 0);
        InsuranceCompany ic = new InsuranceCompany(start);
        Person p = new Person("12345678");
        // premium=10, every 6 months, initial balance 5, next due at start
        ContractPaymentData pd = new ContractPaymentData(10, PremiumPaymentFrequency.SEMI_ANNUAL, start, 5);
        AbstractContract c = new AbstractContract("T1", ic, p, pd, 0) {};

        // First update: at due date, adds 10 → 15, next due = start+6m
        c.updateBalance();
        assertEquals(15, pd.getOutstandingBalance());
        assertEquals(start.plusMonths(6), pd.getNextPaymentTime());

        // Advance time by just over a year (2 cycles): start+1y+1d → charge twice
        ic.setCurrentTime(start.plusYears(1).plusDays(1));
        c.updateBalance();
        assertEquals(35, pd.getOutstandingBalance());  // 15 + 2×10 = 35
        assertEquals(start.plusYears(1).plusMonths(6), pd.getNextPaymentTime());
    }

    @Test
    @DisplayName("Negative prepayment preserved as credit")
    void negativeBalancePrepayment() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        InsuranceCompany ic = new InsuranceCompany(start);
        Person p = new Person("12345678");
        // premium=50 monthly, initial overpayment = -10
        ContractPaymentData pd = new ContractPaymentData(50, PremiumPaymentFrequency.MONTHLY, start, -10);
        AbstractContract c = new AbstractContract("T2", ic, p, pd, 0) {};

        // One update: at due date adds 50 -> -10 + 50 = 40
        c.updateBalance();
        assertEquals(40, pd.getOutstandingBalance());
        assertEquals(start.plusMonths(1), pd.getNextPaymentTime());
    }

}
