package mk.ukim.finki.sdb2026;

import mk.ukim.finki.sdb2026.model.valueObjects.CourseUrl;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class CourseUrlTest {

    @TestFactory
    Stream<DynamicTest> shouldBeAccepted() {
        return Stream.of(
                "https://finki.ukim.mk/subject/wp-2026",
                "https://github.com/user/repo/tree/main",
                "https://elearning.finki.mk/spring-boot-lab",
                "https://courses.finki.mk/course/view.php?id=1234",
                "https://youtube.com/watch?v=dQw4w9WgXcQ",
                "https://docs.google.com/d/1a2b3c_final"
        ).map(url ->
                dynamicTest("CourseUrl: " + url,
                        () -> assertDoesNotThrow(() -> new CourseUrl(url))
                )
        );
    }
}
