import company.InsuranceCompany;
import contracts.MasterVehicleContract;
import contracts.SingleVehicleContract;
import objects.Person;
import objects.Vehicle;
import payment.PremiumPaymentFrequency;

import java.time.LocalDateTime;

public class master {
    public static void main(String[] args) {
        InsuranceCompany insuranceCompany = new InsuranceCompany(LocalDateTime.of(2025, 4, 15, 12, 0));
        Person naturalPerson1 = new Person("8351068242");
        Person naturalPerson2 = new Person("0402114911");
        Person legalPerson1 = new Person("12345678");
        Vehicle vehicle1 = new Vehicle("AA111AA", 15_000);
        Vehicle vehicle2 = new Vehicle("BANAN22", 22_000);
        Vehicle vehicle3 = new Vehicle("SOMRYBA", 8_000);
        Vehicle vehicle4 = new Vehicle("ICOOKED", 40_000);

        SingleVehicleContract c1 = insuranceCompany.insureVehicle("c1", null, legalPerson1, 1500, PremiumPaymentFrequency.ANNUAL,     new Vehicle("AA111AA", 15_000));
        SingleVehicleContract c2 = insuranceCompany.insureVehicle("c2", null, legalPerson1, 184, PremiumPaymentFrequency.MONTHLY,     new Vehicle("BANAN22", 22_000));
        SingleVehicleContract c3 = insuranceCompany.insureVehicle("c3", null, legalPerson1, 400, PremiumPaymentFrequency.SEMI_ANNUAL, new Vehicle("SOMRYBA", 8_000));
        SingleVehicleContract c4 = insuranceCompany.insureVehicle("c4", null, legalPerson1, 1000, PremiumPaymentFrequency.QUARTERLY,  new Vehicle("ICOOKED", 40_000));

        c1.getContractPaymentData().setPremium(30);
        c2.getContractPaymentData().setPremium(50);
        c3.getContractPaymentData().setPremium(75);
        c4.getContractPaymentData().setPremium(20);
        c1.getContractPaymentData().setOutstandingBalance(30);
        c2.getContractPaymentData().setOutstandingBalance(50);
        c3.getContractPaymentData().setOutstandingBalance(100);
        c4.getContractPaymentData().setOutstandingBalance(0);
        MasterVehicleContract m1 = new MasterVehicleContract("m1", insuranceCompany, null, legalPerson1);
        m1.getChildContracts().add(c1);
        m1.getChildContracts().add(c2);
        m1.getChildContracts().add(c3);
        m1.getChildContracts().add(c4);
        insuranceCompany.getContracts().add(m1);
        c4.setInactive();
    }
}
