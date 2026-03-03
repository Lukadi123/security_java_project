package mk.ukim.finki.wp.commonmodel.valueobjects;

import jakarta.persistence.Embeddable;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;

import java.util.Objects;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.*;

/**
 * Value object for Group IDs.
 *
 * BEFORE (vulnerable):
 * - Long groupId (sequential: 1, 2, 3, 4...)
 * - Enumerable: attacker can guess /api/groups/1, /2, /3...
 * - Could be negative
 * - Could be null (ambiguous: no group vs. unknown group?)
 *
 * AFTER (secure):
 * - UUID-based: "7f3b1c2e-8a9d-4e5f-b6c7-8d9e0f1a2b3c"
 * - Non-enumerable (unpredictable)
 * - Format validated
 * - Type-safe (cannot mix GroupId with StudentId)
 *
 * DESIGN DECISION:
 * - This is a VALUE OBJECT, not a DomainObjectId
 * - Why? GroupId might be external (from another system)
 * - We don't generate it, we just validate and store it
 * - If Group was an entity in OUR system, it would extend DomainObjectId
 */
@Embeddable
public class GroupId implements ValueObject {

    private String id;

    /**
     * Creates a GroupId from a UUID string.
     *
     * @param id UUID in standard format
     * @throws NullPointerException if id is null
     * @throws IllegalArgumentException if id format is invalid
     */
    public GroupId(String id) {
        notNull(id, "GroupId must not be null");
        matchesPattern(id,
                "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}",
                "GroupId must be a valid UUID format");
        this.id = id;
    }

    /**
     * JPA no-arg constructor.
     * Protected so application code cannot bypass validation.
     */
    protected GroupId() {
        this.id = null;
    }

    /**
     * Creates a new GroupId with a random UUID.
     *
     * @return a new GroupId with a randomly generated UUID
     */
    public static GroupId randomGroupId() {
        return new GroupId(UUID.randomUUID().toString());
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupId groupId = (GroupId) o;
        return Objects.equals(id, groupId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }
}
