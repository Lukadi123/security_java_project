package mk.ukim.finki.wp.commonmodel.base;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Student {
    @Id
    private String index;

    public Student(String index) {
        this.index = index;
    }

    protected Student() {
    }

    public String getIndex() {
        return index;
    }
}
