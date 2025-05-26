package myTests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import payment.ContractPaymentData;
import payment.PremiumPaymentFrequency;

import java.time.LocalDateTime;

public class ContractPaymentDataTests {

    private LocalDateTime now = LocalDateTime.now();

    @Test
    void testValidContractPaymentDataCreation() {
        ContractPaymentData data = new ContractPaymentData(100, PremiumPaymentFrequency.ANNUAL, now, 0);

        assertEquals(100, data.getPremium());
        assertEquals(PremiumPaymentFrequency.ANNUAL, data.getPremiumPaymentFrequency());
        assertEquals(now, data.getNextPaymentTime());
        assertEquals(0, data.getOutstandingBalance());
    }

    @Test
    void testInvalidPremium() {
        // Nulová hodnota
        assertThrows(IllegalArgumentException.class,
                () -> new ContractPaymentData(0, PremiumPaymentFrequency.ANNUAL, now, 0));

        // Záporná hodnota
        assertThrows(IllegalArgumentException.class,
                () -> new ContractPaymentData(-100, PremiumPaymentFrequency.ANNUAL, now, 0));

        // Test set metódy
        ContractPaymentData data = new ContractPaymentData(100, PremiumPaymentFrequency.ANNUAL, now, 0);
        assertThrows(IllegalArgumentException.class, () -> data.setPremium(0));
        assertThrows(IllegalArgumentException.class, () -> data.setPremium(-10));
    }

    @Test
    void testInvalidFrequency() {
        // Null hodnota
        assertThrows(IllegalArgumentException.class,
                () -> new ContractPaymentData(100, null, now, 0));

        // Test set metódy
        ContractPaymentData data = new ContractPaymentData(100, PremiumPaymentFrequency.ANNUAL, now, 0);
        assertThrows(IllegalArgumentException.class, () -> data.setPremiumPaymentFrequency(null));
    }

    @Test
    void testInvalidNextPaymentTime() {
        // Null hodnota
        assertThrows(IllegalArgumentException.class,
                () -> new ContractPaymentData(100, PremiumPaymentFrequency.ANNUAL, null, 0));
    }

    @Test
    void testUpdateNextPaymentTime() {
        // Test pre ročnú frekvenciu
        ContractPaymentData annualData = new ContractPaymentData(100, PremiumPaymentFrequency.ANNUAL, now, 0);
        annualData.updateNextPaymentTime();
        assertEquals(now.plusMonths(12), annualData.getNextPaymentTime());

        // Test pre polročnú frekvenciu
        ContractPaymentData semiAnnualData = new ContractPaymentData(100, PremiumPaymentFrequency.SEMI_ANNUAL, now, 0);
        semiAnnualData.updateNextPaymentTime();
        assertEquals(now.plusMonths(6), semiAnnualData.getNextPaymentTime());

        // Test pre štvrťročnú frekvenciu
        ContractPaymentData quarterlyData = new ContractPaymentData(100, PremiumPaymentFrequency.QUARTERLY, now, 0);
        quarterlyData.updateNextPaymentTime();
        assertEquals(now.plusMonths(3), quarterlyData.getNextPaymentTime());

        // Test pre mesačnú frekvenciu
        ContractPaymentData monthlyData = new ContractPaymentData(100, PremiumPaymentFrequency.MONTHLY, now, 0);
        monthlyData.updateNextPaymentTime();
        assertEquals(now.plusMonths(1), monthlyData.getNextPaymentTime());
    }
}