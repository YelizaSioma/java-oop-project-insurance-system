package dianaTests;

import objects.Vehicle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleTests {

    @Test
    void validVehicle() {
        Vehicle v = new Vehicle("AB12C34", 8_000);
        assertEquals("AB12C34", v.getLicensePlate());
        assertEquals(8_000, v.getOriginalValue());
    }

    @Test
    void invalidPlateWrongLength() {
        assertThrows(IllegalArgumentException.class, () -> new Vehicle("SHORT", 8_000));
    }

    @Test
    void invalidPlateLowercase() {
        assertThrows(IllegalArgumentException.class, () -> new Vehicle("Ab12C34", 8_000));
    }

    @Test
    void invalidPrice() {
        assertThrows(IllegalArgumentException.class, () -> new Vehicle("AB12C34", 0));
        assertThrows(IllegalArgumentException.class, () -> new Vehicle("AB12C34", -1));
    }
}
