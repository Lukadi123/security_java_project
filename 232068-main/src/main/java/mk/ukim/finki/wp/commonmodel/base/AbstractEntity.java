package mk.ukim.finki.wp.commonmodel.base;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.util.ProxyUtils;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Base class for all domain entities.
 *
 * ENTITY CHARACTERISTICS:
 * - Has a persistent, constant identity (ID)
 * - Two entities with same ID = same object (even if other fields differ)
 * - Mutable (state can change over time, but identity cannot)
 *
 * EXAMPLE:
 * StudentSubjectEnrollment with ID "abc-123" enrolls in "Math"
 * Later, same ID enrolls in "Physics"
 * Still the same enrollment entity (same ID), different state
 *
 * EQUALITY:
 * - Based ONLY on ID, not on other attributes
 * - Product(id=1, name="X") == Product(id=1, name="Y") ✅
 *
 * WHY FINAL ID:
 * - Once created with an ID, that ID never changes
 * - Reflects domain reality: identity is permanent
 *
 * @param <ID> The type of the entity's ID (e.g., StudentSubjectEnrollmentId)
 */
@MappedSuperclass
public abstract class AbstractEntity<ID extends DomainObjectId> implements DomainObject {

    @EmbeddedId
    private ID id;

    /**
     * Constructor for creating a new entity with a given ID.
     *
     * @param id The entity's identifier
     * @throws NullPointerException if id is null
     */
    protected AbstractEntity(ID id) {
        this.id = notNull(id, "id must not be null");
    }

    /**
     * No-arg constructor required by JPA.
     * Protected so only JPA can use it, not application code.
     */
    protected AbstractEntity() {
    }

    public ID getId() {
        return id;
    }

    /**
     * Two entities are equal if they have the same ID.
     *
     * ProxyUtils.getUserClass handles JPA proxies - JPA sometimes wraps
     * entities in proxy objects at runtime for lazy loading.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !getClass().equals(ProxyUtils.getUserClass(obj))) {
            return false;
        }
        var other = (AbstractEntity<?>) obj;
        return id != null && id.equals(other.id);
    }

    /**
     * Hash code based on ID only.
     */
    @Override
    public int hashCode() {
        return id == null ? super.hashCode() : id.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + "}";
    }
}
