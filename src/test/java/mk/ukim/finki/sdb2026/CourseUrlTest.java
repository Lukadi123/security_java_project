package mk.ukim.finki.sdb2026;

import mk.ukim.finki.sdb2026.model.valueObjects.CourseUrl;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.apache.commons.lang3.StringUtils.repeat;
import java.util.function.Supplier;

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
    @TestFactory
    Stream<DynamicTest> shouldNotBeAcceptedInvalidInput() {
        return Stream.of(
                null,
                "",
                " ",
                "\t",
                "\n",
                "javascript:alert(1)",
                "ftp://files.finki.mk/notes.pdf",
                "©@£$∞§|[]≈±´•Ωé®†μüıoeπ˙~ß∂¸√ç‹›''‚…"
        ).map(url ->
                dynamicTest("CourseUrl: " + url,
                        () -> assertThrows(
                                RuntimeException.class,
                                () -> new CourseUrl(url)
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
                "https://a.com/select/all",
                "https://a.com/drop/table",
                "https://a.com/union/query"
        ).map(url ->
                dynamicTest("CourseUrl: " + url,
                        () -> assertThrows(
                                RuntimeException.class,
                                () -> new CourseUrl(url)
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
                .map(urlSupplier ->
                        dynamicTest("CourseUrl length: " + urlSupplier.get().length(),
                                () -> assertThrows(
                                        RuntimeException.class,
                                        () -> new CourseUrl(urlSupplier.get())
                                )
                        )
                );
    }


}
