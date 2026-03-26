package mk.ukim.finki.sdb2026;

import mk.ukim.finki.sdb2026.model.valueObjects.TimeSlot;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class TimeSlotTest {

    @TestFactory
    Stream<DynamicTest> shouldBeAccepted() {
        return Stream.of(
                "Mon 08:00-10:00",
                "Wed 12:15-13:45",
                "Fri 18:00-20:00",
                "Tue + Thu 09:00-10:30",
                "Sat 10:00-12:00 (Lab)",
                "Mon 14:00-16:00 & Wed 14:00-16:00"
        ).map(slot ->
                dynamicTest("TimeSlot: " + slot,
                        () -> assertDoesNotThrow(() -> new TimeSlot(slot))
                )
        );
    }
}
