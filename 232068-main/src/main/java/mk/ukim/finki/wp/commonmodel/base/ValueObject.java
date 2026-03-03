package mk.ukim.finki.wp.commonmodel.base;

/**
 * Marker interface for Value Objects.
 *
 * CHARACTERISTICS:
 * - Immutable (no setters, all fields final)
 * - Defined entirely by their attributes
 * - Two instances with same values are considered equal
 * - No meaningful identity
 *
 * EXAMPLES:
 * - Money(100, EUR) == Money(100, EUR) ✅
 * - GroupName("Group A") == GroupName("Group A") ✅
 *
 * CONTRAST WITH ENTITIES:
 * - Entities have identity (same ID = same object, even if attributes differ)
 * - Value objects have no identity (same attributes = same object)
 *
 * WHY IMMUTABLE:
 * - Prevents bugs from unexpected state changes
 * - Thread-safe by design
 * - Easier to reason about
 */
public interface ValueObject extends DomainObject {
}
