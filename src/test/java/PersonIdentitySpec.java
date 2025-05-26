
import objects.Person;
import objects.LegalForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Person ID Type Detection")
public class PersonIdentitySpec {

    @ParameterizedTest
    @ValueSource(strings = {
            "530101123",   // 9-digit RČ born 1 Jan 1953
            "8351068242",  // 10-digit RČ (with checksum)
            "530101123",   // female RČ: month 62→12
            "8351068242"   // arbitrary valid 10-digit RČ
    })
    @DisplayName("Valid RČs are detected as NATURAL")
    void validRcAreNatural(String rc) {
        Person p = new Person(rc);
        assertEquals(LegalForm.NATURAL, p.getLegalForm(), () -> rc + " should be NATURAL");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123456",   // 6-digit IČO
            "87654321", // 8-digit IČO
            "00000123", // leading zeros
    })
    @DisplayName("Valid IČOs are detected as LEGAL")
    void validIcoAreLegal(String ico) {
        Person p = new Person(ico);
        assertEquals(LegalForm.LEGAL, p.getLegalForm(), () -> ico + " should be LEGAL");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",             // empty
            "abcdef123",    // letters
            "1234567",      // 7-char (neither 6,8 nor 9,10)
            "123456789012", // too long
            "5402312345",   // bad date Feb 31
            "5401010001"    // bad checksum (if it doesn’t satisfy mod 11)
    })
    @DisplayName("Invalid IDs throw IllegalArgumentException")
    void invalidIdsThrow(String bad) {
        assertThrows(IllegalArgumentException.class, () -> new Person(bad));
    }

    @Test
    @DisplayName("Null ID throws IllegalArgumentException")
    void nullIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Person(null));
    }
}
