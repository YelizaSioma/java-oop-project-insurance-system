package myTests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import objects.Vehicle;

public class VehicleTests {

    @Test
    void testValidVehicleCreation() {
        Vehicle vehicle = new Vehicle("BA123CD", 10000);
        assertEquals("BA123CD", vehicle.getLicensePlate());
        assertEquals(10000, vehicle.getOriginalValue());
    }

    @Test
    void testInvalidLicensePlate() {
        // Null
        assertThrows(IllegalArgumentException.class, () -> new Vehicle(null, 10000));

        // Prázdny reťazec
        assertThrows(IllegalArgumentException.class, () -> new Vehicle("", 10000));

        // Nesprávna dĺžka
        assertThrows(IllegalArgumentException.class, () -> new Vehicle("BA123C", 10000));
        assertThrows(IllegalArgumentException.class, () -> new Vehicle("BA123CDE", 10000));

        // Malé písmená
        assertThrows(IllegalArgumentException.class, () -> new Vehicle("ba123cd", 10000));

        // Nepovolené znaky
        assertThrows(IllegalArgumentException.class, () -> new Vehicle("BA123C!", 10000));
    }

    @Test
    void testInvalidOriginalValue() {
        // Nulová hodnota
        assertThrows(IllegalArgumentException.class, () -> new Vehicle("BA123CD", 0));

        // Záporná hodnota
        assertThrows(IllegalArgumentException.class, () -> new Vehicle("BA123CD", -1000));
    }

    @Test
    void testImmutability() {
        Vehicle vehicle = new Vehicle("BA123CD", 10000);

        // Kontrola, že polia sú final (kompilácia by zlyhala, ak by neboli)
        // Toto je skôr kontrola implementácie, nie priamo testovateľné tu
    }
}