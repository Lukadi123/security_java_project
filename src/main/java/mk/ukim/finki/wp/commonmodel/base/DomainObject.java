package mk.ukim.finki.wp.commonmodel.base;

import java.io.Serializable;

/**
 * Marker interface for all domain objects.
 *
 * PURPOSE:
 * - Semantically identifies classes as first-class citizens of the domain
 * - Not a utility or infrastructure class
 * - Extends Serializable for JPA compatibility
 *
 * WHY THIS MATTERS:
 * - Clear separation between domain logic and technical infrastructure
 * - Makes it explicit that this class represents a business concept
 */
public interface DomainObject extends Serializable {
}
