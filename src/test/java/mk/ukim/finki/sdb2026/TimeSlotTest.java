package mk.ukim.finki.sdb2026;

import mk.ukim.finki.sdb2026.model.valueObjects.TimeSlot;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.apache.commons.lang3.StringUtils.repeat;

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
    @TestFactory
    Stream<DynamicTest> shouldBeAcceptedBoundaryCases() {
        return Stream.of(
                "Mo 08",
                "F 8:0",
                "Mon 08:00-10:00 & Wed 08:00-10:00 + Fri 08:00-10:00 (Grp A)",
                "Sat 22:00-23:00",
                "Mon (A)"
        ).map(slot ->
                dynamicTest("TimeSlot: " + slot,
                        () -> assertDoesNotThrow(() -> new TimeSlot(slot))
                )
        );
    }

    @TestFactory
    Stream<DynamicTest> shouldNotBeAcceptedBoundaryCases() {
        return Stream.of(
                "M 08",
                repeat("X", 61),
                "Mon 08:00-10:00 #Lab",
                "Fri 10:00-12:00 @Room",
                "Wed 09:00-11:00 50% full"
        ).map(slot ->
                dynamicTest("TimeSlot: " + slot,
                        () -> assertThrows(
                                IllegalArgumentException.class,
                                () -> new TimeSlot(slot)
                        )
                )
        );
    }

    @TestFactory
    Stream<DynamicTest> shouldNotBeAcceptedInvalidInput() {
        return Stream.of(
                null,
                "",
                " ",
                "\t",
                "\n",
                "Mon",
                "©@£$∞§|[]≈±´•Ωé®†μüıoeπ˙~ß∂¸√ç‹›''‚…",
                "\"=0@$*^%;<!>.:\\\\()&#\\\"\","
        ).map(slot ->
                dynamicTest("TimeSlot: " + slot,
                        () -> assertThrows(
                                RuntimeException.class,
                                () -> new TimeSlot(slot)
                        )
                )
        );
    }

    @TestFactory
    Stream<DynamicTest> shouldRejectSql() {
        return Stream.of(
                "'or%20select *",
                "admin'--",
                "<>\"'%;)(&+",
                "'%20or%20''='",
                "'%20or%20'x'='x",
                "\"%20or%20\"x\"=\"x",
                "')%20or%20('x'='x",
                "0 or 1=1",
                "' or 0=0 ",
                "\" or 0=0 "
        ).map(slot ->
                dynamicTest("TimeSlot: " + slot,
                        () -> assertThrows(
                                RuntimeException.class,
                                () -> new TimeSlot(slot)
                        )
                )
        );
    }


}
