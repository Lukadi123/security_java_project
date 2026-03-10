package mk.ukim.finki.wp.commonmodel.valueobjects;

import jakarta.persistence.Embeddable;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.*;

@Embeddable
public class NumberOfEnrollments implements ValueObject {

    public static final int MIN_ENROLLMENTS = 1;
    public static final int MAX_ENROLLMENTS = 10;

    private Short value;

    public NumberOfEnrollments(Short value) {
        notNull(value, "NumberOfEnrollments must not be null");
        inclusiveBetween((short) MIN_ENROLLMENTS, (short) MAX_ENROLLMENTS, value,
                "NumberOfEnrollments must be between " + MIN_ENROLLMENTS + " and " + MAX_ENROLLMENTS +
                        ", got: " + value);
        this.value = value;
    }

    public NumberOfEnrollments(int value) {
        this((short) value);
    }

    protected NumberOfEnrollments() {
        this.value = null;
    }

    public NumberOfEnrollments increment() {
        isTrue(value < MAX_ENROLLMENTS,
                "Cannot increment: already at maximum enrollments (" + MAX_ENROLLMENTS + ")");
        return new NumberOfEnrollments((short) (value + 1));
    }

    public boolean canEnrollAgain() {
        return value < MAX_ENROLLMENTS;
    }

    public Short getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumberOfEnrollments that = (NumberOfEnrollments) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value + " enrollment(s)";
    }
}
