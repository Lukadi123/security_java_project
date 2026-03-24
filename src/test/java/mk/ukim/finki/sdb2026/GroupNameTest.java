package mk.ukim.finki.sdb2026;

import mk.ukim.finki.sdb2026.model.valueObjects.GroupName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
}