package mk.ukim.finki.sdb2026;

import mk.ukim.finki.sdb2026.model.valueObjects.GroupName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class GroupNameTest {

    @TestFactory
    Stream<DynamicTest> shouldBeAccepted() {
        return Stream.of(
                "Group A & B",
                "Lab: Section 2",
                "Tuesday + Thursday PM",
                "Lab A = Advanced Group",
                "Section 1.2",
                "Morning Lab"
        ).map(name ->
                dynamicTest("Group name: " + name,
                        () -> assertDoesNotThrow(() -> new GroupName(name))
                )
        );
    }

    @TestFactory
    Stream<DynamicTest> shouldBeAcceptedBoundaryCases() {
        return Stream.of(
                "A",
                "G1",
                "Lab/2",
                "Group A: Advanced & Specialized Lab Section",
                "Lab 1/2 - Morning + Evening: Group A = Section B"
        ).map(name ->
                dynamicTest("Group name: " + name,
                        () -> assertDoesNotThrow(() -> new GroupName(name))
                )
        );
    }

    @TestFactory
    Stream<DynamicTest> shouldNotBeAcceptedBoundaryCases() {
        return Stream.of(
                "",
                repeat("X", 51),
                "Group@A",
                "Lab#2",
                "Section%1"
        ).map(name ->
                dynamicTest("Group name: " + name,
                        () -> assertThrows(
                                IllegalArgumentException.class,
                                () -> new GroupName(name)
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
                "©@£$∞§|[]≈±´•Ωé®†μüıoeπ˙~ß∂¸√ç‹›''‚…",
                "\"=0@$*^%;<!>.:\\\\()&#\\\"\","
        ).map(name ->
                dynamicTest("Group name: " + name,
                        () -> assertThrows(
                                RuntimeException.class,
                                () -> new GroupName(name)
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
        ).map(name ->
                dynamicTest("Group name: " + name,
                        () -> assertThrows(
                                RuntimeException.class,
                                () -> new GroupName(name)
                        )
                )
        );
    }
    @TestFactory
    Stream<DynamicTest> shouldRejectExtremeInput() {
        return Stream.<Supplier<String>>of(
                        () -> repeat("X", 10_000),
                        () -> repeat("X", 100_000),
                        () -> repeat("X", 1_000_000),
                        () -> repeat("X", 10_000_000),
                        () -> repeat("X", 20_000_000),
                        () -> repeat("X", 40_000_000))
                .map(nameSupplier ->
                        dynamicTest("Group name length: " + nameSupplier.get().length(),
                                () -> assertThrows(
                                        RuntimeException.class,
                                        () -> new GroupName(nameSupplier.get())
                                )
                        )
                );
    }
}

