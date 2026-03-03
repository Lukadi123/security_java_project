package mk.ukim.finki.wp.commonmodel.base;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Stub entity for Subject.
 */
@Entity
public class Subject {
    @Id
    private String id; // e.g., "MATH101"

    public Subject(String id) {
        this.id = id;
    }

    protected Subject() {
    }

    public String getId() {
        return id;
    }
}
