package mk.ukim.finki.wp.commonmodel.teachingallocation;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class JoinedSubject {
    @Id
    private String id;

    public JoinedSubject(String id) {
        this.id = id;
    }

    protected JoinedSubject() {
    }

    public String getId() {
        return id;
    }
}
