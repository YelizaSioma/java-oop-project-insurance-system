package myTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import objects.LegalForm;
import objects.Person;
import contracts.AbstractContract;

import java.util.Set;

public class PersonTests {

    private Person naturalPerson;
    private Person legalPerson;

    @BeforeEach
    void setUp() {
        // Platné rodné číslo - muž narodený 1.1.1990
        naturalPerson = new Person("9001010018");
        // Platné IČO - 8 číslic
        legalPerson = new Person("12345678");
    }

    @Test
    void testValidPersonCreation() {
        assertEquals("9001010018", naturalPerson.getId());
        assertEquals(LegalForm.NATURAL, naturalPerson.getLegalForm());
        assertEquals(0, naturalPerson.getPaidOutAmount());
        assertNotNull(naturalPerson.getContracts());

        assertEquals("12345678", legalPerson.getId());
        assertEquals(LegalForm.LEGAL, legalPerson.getLegalForm());
    }

    @Test
    void testValidRodneČislo() {
        // 10-miestne rodné číslo
        assertDoesNotThrow(() -> new Person("9001010018"));

        // 9-miestne rodné číslo (pred 1954)
        assertDoesNotThrow(() -> new Person("400101001"));

        // Žena (mesiac+50)
        assertDoesNotThrow(() -> new Person("7354196762"));
    }

    @Test
    void testInvalidRodneČislo() {
        // Null
        assertThrows(IllegalArgumentException.class, () -> new Person(null));

        // Prázdny reťazec
        assertThrows(IllegalArgumentException.class, () -> new Person(""));

        // Nesprávna dĺžka
        assertThrows(IllegalArgumentException.class, () -> new Person("12345"));

        // Neplatný mesiac
        assertThrows(IllegalArgumentException.class, () -> new Person("9013010018"));

        // Neplatný deň
        assertThrows(IllegalArgumentException.class, () -> new Person("9001320018"));

        // Neplatná kontrolná suma pre 10-miestne rodné číslo
        assertThrows(IllegalArgumentException.class, () -> new Person("9001010019"));

        // Písmená namiesto číslic
        assertThrows(IllegalArgumentException.class, () -> new Person("90010100AB"));
    }

    @Test
    void testValidIČO() {
        // 8-miestne IČO
        assertDoesNotThrow(() -> new Person("12345678"));

        // 6-miestne IČO
        assertDoesNotThrow(() -> new Person("123456"));
    }

    @Test
    void testInvalidIČO() {
        // Nesprávna dĺžka
        assertThrows(IllegalArgumentException.class, () -> new Person("1234567"));
        assertThrows(IllegalArgumentException.class, () -> new Person("123456789"));

        // Písmená namiesto číslic
        assertThrows(IllegalArgumentException.class, () -> new Person("1234A6"));
    }

    @Test
    void testPayout() {
        // Test správneho pripočítania vyplatenej sumy
        naturalPerson.payout(100);
        assertEquals(100, naturalPerson.getPaidOutAmount());

        naturalPerson.payout(50);
        assertEquals(150, naturalPerson.getPaidOutAmount());

        // Test neplatnej sumy
        assertThrows(IllegalArgumentException.class, () -> naturalPerson.payout(0));
        assertThrows(IllegalArgumentException.class, () -> naturalPerson.payout(-10));
    }

}