package NikoTests;

import objects.Person;
import objects.LegalForm;
import payment.ContractPaymentData;
import payment.PremiumPaymentFrequency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Domain Objects Validation")
public class DomainObjectTests {

    // --- Person tests ---

    @ParameterizedTest
    @ValueSource(strings = {"8351068242", "530101123"})
    @DisplayName("Valid natural-person RČ accepted")
    void validNaturalPersonRc(String rc) {
        Person p = new Person(rc);
        assertEquals(LegalForm.NATURAL, p.getLegalForm());
        assertEquals(rc, p.getId());
        assertTrue(p.getPaidOutAmount() == 0);
        assertTrue(p.getContracts().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456", "87654321"})
    @DisplayName("Valid legal-person IČO accepted")
    void validLegalPersonIco(String ico) {
        Person p = new Person(ico);
        assertEquals(LegalForm.LEGAL, p.getLegalForm());
        assertEquals(ico, p.getId());
    }

    @Test
    @DisplayName("Invalid Person IDs throw")
    void invalidPersonIdsThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Person(null));
        assertThrows(IllegalArgumentException.class, () -> new Person(""));
        assertThrows(IllegalArgumentException.class, () -> new Person("ABCDE1234"));
        assertThrows(IllegalArgumentException.class, () -> new Person("123"));  // too short
    }

    @Test
    @DisplayName("payout accumulates and rejects non-positive")
    void payoutAccumulatesAndRejectsNonPositive() {
        Person p = new Person("8351068242");
        p.payout(100);
        assertEquals(100, p.getPaidOutAmount());
        assertThrows(IllegalArgumentException.class, () -> p.payout(0));
        assertThrows(IllegalArgumentException.class, () -> p.payout(-5));
    }

    @Test
    @DisplayName("addContract rejects null")
    void addContractRejectsNull() {
        Person p = new Person("8351068242");
        assertThrows(IllegalArgumentException.class, () -> p.addContract(null));
    }

    // --- PremiumPaymentFrequency tests ---

    @Test
    @DisplayName("PremiumPaymentFrequency values in months")
    void premiumPaymentFrequencyValues() {
        assertEquals(12, PremiumPaymentFrequency.ANNUAL.getValueInMonths());
        assertEquals(6,  PremiumPaymentFrequency.SEMI_ANNUAL.getValueInMonths());
        assertEquals(3,  PremiumPaymentFrequency.QUARTERLY.getValueInMonths());
        assertEquals(1,  PremiumPaymentFrequency.MONTHLY.getValueInMonths());
    }

    // --- ContractPaymentData tests ---

    @Test
    @DisplayName("Constructor rejects invalid arguments")
    void contractPaymentDataConstructorRejectsInvalid() {
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class,
                () -> new ContractPaymentData(0, PremiumPaymentFrequency.MONTHLY, now, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new ContractPaymentData(10, null, now, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new ContractPaymentData(10, PremiumPaymentFrequency.MONTHLY, null, 0));
    }

    @Test
    @DisplayName("Setters reject invalid values")
    void contractPaymentDataSettersReject() {
        LocalDateTime now = LocalDateTime.now();
        var cpd = new ContractPaymentData(10, PremiumPaymentFrequency.MONTHLY, now, 5);

        assertThrows(IllegalArgumentException.class, () -> cpd.setPremium(0));
        assertThrows(IllegalArgumentException.class, () -> cpd.setPremiumPaymentFrequency(null));
        // outstandingBalance may be negative or positive per spec – no exception
        cpd.setOutstandingBalance(-100);
        assertEquals(-100, cpd.getOutstandingBalance());
    }

    @Test
    @DisplayName("updateNextPaymentTime advances correctly")
    void updateNextPaymentTimeAdvances() {
        LocalDateTime base = LocalDateTime.of(2025, 1, 15, 12, 0);
        var cpd = new ContractPaymentData(50, PremiumPaymentFrequency.SEMI_ANNUAL, base, 0);

        cpd.updateNextPaymentTime();
        assertEquals(base.plusMonths(6), cpd.getNextPaymentTime());

        cpd.updateNextPaymentTime();
        assertEquals(base.plusMonths(12), cpd.getNextPaymentTime());
    }
}
