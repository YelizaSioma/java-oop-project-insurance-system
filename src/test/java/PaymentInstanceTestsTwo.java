package dianaTests;

import payment.PaymentInstance;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentInstanceTests {

    @Test
    void validInstance() {
        PaymentInstance p = new PaymentInstance(LocalDateTime.now(), 42);
        assertEquals(42, p.getPaymentAmount());
    }

    @Test
    void invalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new PaymentInstance(null, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new PaymentInstance(LocalDateTime.now(), 0));
    }
}
