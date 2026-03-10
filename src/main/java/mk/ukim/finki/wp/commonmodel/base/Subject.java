package mk.ukim.finki.wp.commonmodel.base;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Subject {
    @Id
    private String id;

    public Subject(String id) {
        this.id = id;
    }

    protected Subject() {
    }

    public String getId() {
        return id;
    }
}
