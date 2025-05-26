package myTests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import payment.PaymentInstance;

import java.time.LocalDateTime;

public class PaymentInstanceTests {

    private LocalDateTime now = LocalDateTime.now();

    @Test
    void testValidPaymentInstanceCreation() {
        PaymentInstance instance = new PaymentInstance(now, 100);

        assertEquals(now, instance.getPaymentTime());
        assertEquals(100, instance.getPaymentAmount());
    }

    @Test
    void testInvalidPaymentTime() {
        // Null hodnota
        assertThrows(IllegalArgumentException.class,
                () -> new PaymentInstance(null, 100));
    }

    @Test
    void testInvalidPaymentAmount() {
        // Nulová hodnota
        assertThrows(IllegalArgumentException.class,
                () -> new PaymentInstance(now, 0));

        // Záporná hodnota
        assertThrows(IllegalArgumentException.class,
                () -> new PaymentInstance(now, -100));
    }

    @Test
    void testCompareTo() {
        PaymentInstance instance1 = new PaymentInstance(now, 100);
        PaymentInstance instance2 = new PaymentInstance(now.plusHours(1), 100);
        PaymentInstance instance3 = new PaymentInstance(now.minusHours(1), 100);

        // Overenie správneho porovnania podľa času
        assertTrue(instance1.compareTo(instance2) < 0); // instance1 je starší než instance2
        assertTrue(instance2.compareTo(instance1) > 0); // instance2 je novší než instance1
        assertTrue(instance3.compareTo(instance1) < 0); // instance3 je starší než instance1
        assertEquals(-1, instance1.compareTo(new PaymentInstance(now, 200))); // rovnaký čas, iná suma
    }
}
