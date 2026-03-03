package mk.ukim.finki.wp.commonmodel.valueobjects;

import jakarta.persistence.Embeddable;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.*;

/**
 * Value object for group names (e.g., "Group A", "Lab 2", "Tuesday PM").
 *
 * BEFORE (vulnerable):
 * - String groupName
 * - Could be empty: ""
 * - Could contain SQL: "'; DROP TABLE students;--"
 * - Could be excessively long
 * - No format rules
 *
 * AFTER (secure):
 * - 1-50 characters
 * - Alphanumeric + spaces + limited punctuation
 * - Prevents SQL injection via character whitelist
 * - Meaningful validation error messages
 *
 * VALIDATION ORDER:
 * 1. Null check
 * 2. Length check
 * 3. Pattern check (most expensive)
 *
 * EXAMPLES:
 * ✅ "Group A"
 * ✅ "Lab-2"
 * ✅ "Tuesday PM"
 * ✅ "Section 1.2"
 * ❌ "" (too short)
 * ❌ "Group'; DROP TABLE--" (invalid characters)
 * ❌ "A".repeat(51) (too long)
 */
@Embeddable
public class GroupName implements ValueObject {

    private String name;

    /**
     * Creates a validated GroupName.
     *
     * @param name The group name (1-50 characters, alphanumeric + spaces + .-/)
     * @throws NullPointerException if name is null
     * @throws IllegalArgumentException if name is invalid
     */
    public GroupName(String name) {
        // 1. Null check (cheapest)
        notNull(name, "GroupName must not be null");

        // 2. Size check (cheap)
        inclusiveBetween(1, 50, name.length(),
                "GroupName must be between 1 and 50 characters, got: " + name.length());

        // 3. Pattern check (expensive)
        // Allow: letters, digits, spaces, hyphen, dot, slash
        matchesPattern(name, "^[A-Za-z0-9 .\\-/]+$",
                "GroupName can only contain letters, digits, spaces, and the following: . - /");

        this.name = name;
    }

    /**
     * JPA no-arg constructor.
     */
    protected GroupName() {
        this.name = null;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupName groupName = (GroupName) o;
        return Objects.equals(name, groupName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
