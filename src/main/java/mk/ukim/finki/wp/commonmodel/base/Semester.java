package mk.ukim.finki.wp.commonmodel.base;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Stub entity for Semester.
 * In a real system, this would be fully implemented.
 */
@Entity
public class Semester {
    @Id
    private String code; // e.g., "2024-W" for Winter 2024

    public Semester(String code) {
        this.code = code;
    }

    protected Semester() {
    }

    public String getCode() {
        return code;
    }
}
