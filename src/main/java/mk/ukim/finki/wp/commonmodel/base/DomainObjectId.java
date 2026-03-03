package mk.ukim.finki.wp.commonmodel.base;

import jakarta.persistence.Embeddable;
import jakarta.persistence.MappedSuperclass;

import java.util.Objects;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.matchesPattern;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Base class for all domain object IDs.
 *
 * SECURITY BENEFIT:
 * - Uses UUIDs instead of sequential integers (1, 2, 3...)
 * - Sequential IDs are enumerable: if /api/enrollment/1 exists, try /2, /3...
 * - UUIDs are unpredictable: 550e8400-e29b-41d4-a716-446655440000
 *
 * VALIDATION:
 * - Ensures ID format is correct before construction
 * - Invalid IDs cannot exist in the system
 *
 * JPA ANNOTATIONS:
 * - @MappedSuperclass: JPA recognizes this as inheritable
 * - @Embeddable: Can be embedded into entities
 */
@MappedSuperclass
@Embeddable
public class DomainObjectId implements ValueObject {

    private String id;

    /**
     * Constructor for restoring an ID from a string (e.g., from database).
     *
     * @param uuid The UUID string in standard format
     * @throws NullPointerException if uuid is null
     * @throws IllegalArgumentException if uuid format is invalid
     */
    protected DomainObjectId(String uuid) {
        notNull(uuid, "uuid must not be null");
        matchesPattern(uuid,
                "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}",
                "The UUID must be in a valid format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
        this.id = uuid;
    }

    /**
     * Constructor for generating a new random UUID.
     * Used when creating new domain objects.
     */
    protected DomainObjectId() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainObjectId that = (DomainObjectId) o;
        return Objects.equals(id, that.id);
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
