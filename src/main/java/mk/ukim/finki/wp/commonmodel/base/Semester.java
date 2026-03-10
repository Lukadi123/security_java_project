package mk.ukim.finki.wp.commonmodel.base;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Semester {
    @Id
    private String code;

    public Semester(String code) {
        this.code = code;
    }

    protected Semester() {
    }

    public String getCode() {
        return code;
    }
}
