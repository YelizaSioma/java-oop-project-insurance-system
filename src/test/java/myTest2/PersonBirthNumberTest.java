package myTest2;


import static org.junit.jupiter.api.Assertions.*;

import objects.Person;
import org.junit.jupiter.api.Test;

class PersonBirthNumberTest {

    @Test
    void valid9DigitBirthNumber() {
        assertTrue(Person.isValidBirthNumber("530101123"));
    }

    @Test
    void invalid9DigitBirthNumberWrongYear() {
        assertFalse(Person.isValidBirthNumber("540101123"));
    }

    @Test
    void valid10DigitBirthNumber() {
        assertTrue(Person.isValidBirthNumber("0456281232")); // 2004-06-28, kontrolná suma OK
    }


    @Test
    void invalid10DigitBirthNumberWrongChecksum() {
        assertFalse(Person.isValidBirthNumber("0456231235"));
    }

    @Test
    void invalidBirthNumberWrongFormatLetters() {
        assertFalse(Person.isValidBirthNumber("04562312A4"));
    }

    @Test
    void invalidBirthNumberWrongLengthYearTooHigh() {
        assertFalse(Person.isValidBirthNumber("541231123")); // 54 > 53
    }


    @Test
    void validFemaleBirthNumber() {
        assertTrue(Person.isValidBirthNumber("9956119998"));
    }



    @Test
    void invalidMonth() {
        assertFalse(Person.isValidBirthNumber("9900131234")); // mesiac 00
    }

    @Test
    void invalidDay() {
        assertFalse(Person.isValidBirthNumber("9913321234")); // deň 32
    }

    @Test
    void nullInput() {
        assertFalse(Person.isValidBirthNumber(null));
    }

    @Test
    void emptyInput() {
        assertFalse(Person.isValidBirthNumber(""));
    }
}
