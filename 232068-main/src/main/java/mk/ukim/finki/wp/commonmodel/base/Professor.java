package mk.ukim.finki.wp.commonmodel.base;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Stub entity for Professor.
 */
@Entity
public class Professor {
    @Id
    private String id;

    public Professor(String id) {
        this.id = id;
    }

    protected Professor() {
    }

    public String getId() {
        return id;
    }
}
