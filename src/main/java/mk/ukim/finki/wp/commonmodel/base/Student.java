package mk.ukim.finki.wp.commonmodel.base;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Stub entity for Student.
 */
@Entity
public class Student {
    @Id
    private String index; // e.g., "211234"

    public Student(String index) {
        this.index = index;
    }

    protected Student() {
    }

    public String getIndex() {
        return index;
    }
}
