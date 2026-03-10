package mk.ukim.finki.wp.commonmodel.teachingallocation.schedule;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Course {
    @Id
    private String id;

    public Course(String id) {
        this.id = id;
    }

    protected Course() {
    }

    public String getId() {
        return id;
    }
}
