package mk.ukim.finki.wp.commonmodel.valueobjects;

import jakarta.persistence.Embeddable;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.*;

/**
 * Value object for number of enrollment attempts.
 *
 * BEFORE (vulnerable):
 * - Short numEnrollments
 * - Could be negative: -5 enrollments?
 * - Could be zero: 0 enrollments while having an enrollment record?
 * - Could be 32,767 (Short.MAX_VALUE)
 * - No business rule enforcement
 *
 * AFTER (secure):
 * - Must be between 1 and 10 (business rule)
 * - Negative values impossible
 * - Unreasonable values impossible
 * - Domain operations built-in
 *
 * BUSINESS RULES:
 * - Student must have at least 1 enrollment attempt (minimum)
 * - University policy: max 10 attempts per subject (maximum)
 * - After 10 attempts, student cannot re-enroll
 *
 * IMMUTABILITY:
 * - No setters
 * - increment() returns NEW instance
 * - Original instance unchanged
 */
@Embeddable
public class NumberOfEnrollments implements ValueObject {

    public static final int MIN_ENROLLMENTS = 1;
    public static final int MAX_ENROLLMENTS = 10;

    private Short value;

    /**
     * Creates a validated NumberOfEnrollments.
     *
     * @param value Number of enrollment attempts (1-10)
     * @throws NullPointerException if value is null
     * @throws IllegalArgumentException if value is out of range
     */
    public NumberOfEnrollments(Short value) {
        notNull(value, "NumberOfEnrollments must not be null");
        inclusiveBetween(MIN_ENROLLMENTS, MAX_ENROLLMENTS, value.intValue(),
                "NumberOfEnrollments must be between " + MIN_ENROLLMENTS + " and " + MAX_ENROLLMENTS +
                        ", got: " + value);
        this.value = value;
    }

    public NumberOfEnrollments(int value) {
        notNull((Short)(short) value, "NumberOfEnrollments must not be null");
        inclusiveBetween(MIN_ENROLLMENTS, MAX_ENROLLMENTS, value,
                "NumberOfEnrollments must be between " + MIN_ENROLLMENTS + " and " + MAX_ENROLLMENTS +
                        ", got: " + value);
        this.value = (short) value;
    }
    /**
     * JPA no-arg constructor.
     */
    protected NumberOfEnrollments() {
        this.value = null;
    }

    /**
     * Domain operation: increment enrollment count.
     * Returns a NEW instance (immutability).
     *
     * @return New NumberOfEnrollments with value + 1
     * @throws IllegalArgumentException if already at maximum (10)
     */
    public NumberOfEnrollments increment() {
        isTrue(value < MAX_ENROLLMENTS,
                "Cannot increment: already at maximum enrollments (" + MAX_ENROLLMENTS + ")");
        return new NumberOfEnrollments((short) (value + 1));
    }

    /**
     * Check if student can enroll again.
     *
     * @return true if under max limit, false otherwise
     */
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
