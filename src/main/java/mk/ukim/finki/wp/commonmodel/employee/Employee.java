package mk.ukim.finki.wp.commonmodel.employee;

import jakarta.persistence.*;
import lombok.Getter;
import mk.ukim.finki.wp.commonmodel.base.AbstractEntity;
import mk.ukim.finki.wp.commonmodel.valueobjects.EmployeeId;

@Getter
@Entity
public class Employee extends AbstractEntity<EmployeeId> {

    @Embedded
    private EmployeeState state;

    private String name;

    protected Employee(String name) {
        super(new EmployeeId());
        this.name = name;
        this.state = new EmployeeState();
    }

    protected Employee() {
        super();
    }

    public void promote() {
        state.promote();
    }

    public void terminate() {
        state.terminate();
    }

    public EmployeeStatus getEmployeeStatus() {
        return state.getEmployeeStatus();
    }
}
