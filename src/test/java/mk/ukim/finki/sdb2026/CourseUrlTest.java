package mk.ukim.finki.sdb2026;

import mk.ukim.finki.sdb2026.model.valueObjects.CourseUrl;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.apache.commons.lang3.StringUtils.repeat;

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
    @TestFactory
    Stream<DynamicTest> shouldBeAcceptedBoundaryCases() {
        return Stream.of(
                "https://a.mk",
                "https://x.io/y",
                "https://finki.mk/c?id=1&lang=en",
                "https://a.b/c_d",
                "https://finki.ukim.mk/" + repeat("a", 170)
        ).map(url ->
                dynamicTest("CourseUrl: " + url,
                        () -> assertDoesNotThrow(() -> new CourseUrl(url))
                )
        );
    }

    @TestFactory
    Stream<DynamicTest> shouldNotBeAcceptedBoundaryCases() {
        return Stream.of(
                "https://a",
                "https://" + repeat("x", 193),
                "http://finki.mk/course",
                "https://finki.mk/course page",
                "https://finki.mk/<script>"
        ).map(url ->
                dynamicTest("CourseUrl: " + url,
                        () -> assertThrows(
                                IllegalArgumentException.class,
                                () -> new CourseUrl(url)
                        )
                )
        );
    }

}
