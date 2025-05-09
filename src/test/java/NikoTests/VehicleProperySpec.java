package NikoTests;

import company.InsuranceCompany;
import contracts.SingleVehicleContract;
import objects.Person;
import objects.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import payment.ContractPaymentData;
import payment.PremiumPaymentFrequency;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class VehiclePropertySpec {

    @ParameterizedTest
    @ValueSource(strings = {
            "AAAAAAA","ZZZZZZZ","1234567","A1B2C3D","0A9Z8Y7"
    })
    @DisplayName("Some random 7-char plates are accepted")
    void randomPlatesAreValid(String plate) {
        // no exception → getLicensePlate() round-trips
        Vehicle v = new Vehicle(plate, 1);
        assertEquals(plate, v.getLicensePlate());
    }
    @Test
    @DisplayName("Brute-force uppercase/digit plates")
    void bruteForcePlates() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < 1000; i++) {
            // pseudo-random but reproducible
            long seed = 1234L + i;
            Random rnd = new Random(seed);
            StringBuilder sb = new StringBuilder(7);
            for (int j = 0; j < 7; j++) {
                sb.append(chars.charAt(rnd.nextInt(chars.length())));
            }
            String plate = sb.toString();
            assertDoesNotThrow(() -> new Vehicle(plate, 1), "Failed on " + plate);
        }
    }

    @TestFactory
    @DisplayName("Dynamic Person ID checks")
    Stream<DynamicTest> dynamicPersonChecks() {
        // a mix of known-valid and known-invalid cases
        List<String> ids = List.of(
                "530101123",   // valid 9-digit
                "8351068242",  // valid 10-digit
                "5401010001",  // invalid checksum
                "536702001",   // invalid month=67
                "123456"       // valid ICO
        );

        return ids.stream()
                .map(id -> DynamicTest.dynamicTest("ID=" + id, () -> {
                    boolean expectValid = switch (id) {
                        case "530101123","8351068242","123456" -> true;
                        default -> false;
                    };
                    if (expectValid) {
                        assertDoesNotThrow(() -> new Person(id));
                    } else {
                        assertThrows(IllegalArgumentException.class, () -> new Person(id));
                    }
                }));
    }


    @ParameterizedTest(name = "{0} quarterly cycles past due yields {1}")
    @CsvSource({
            "1, 2",
            "2, 3",
            "5, 6"
    })
    void updateBalanceSkipsExactCycles(int cycles, int expected) {
        LocalDateTime now = LocalDateTime.of(2025,1,1,0,0);
        InsuranceCompany ic = new InsuranceCompany(now);
        Person p = new Person("530101123");
        LocalDateTime due = now.minusMonths(3L * cycles);
        ContractPaymentData pd = new ContractPaymentData(10, PremiumPaymentFrequency.QUARTERLY, due, 0);
        SingleVehicleContract c = new SingleVehicleContract("c", ic, null, p, pd, 0, new Vehicle("ABCDEFG",1000));
        c.updateBalance();
        assertEquals(expected * 10,
                c.getContractPaymentData().getOutstandingBalance());
    }


}
