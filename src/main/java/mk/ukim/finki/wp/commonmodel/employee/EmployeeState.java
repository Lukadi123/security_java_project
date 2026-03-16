package mk.ukim.finki.wp.commonmodel.employee;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;

import static org.apache.commons.lang3.Validate.isTrue;

@Embeddable
public class EmployeeState implements ValueObject {

    @Enumerated(EnumType.STRING)
    private EmployeeStatus employeeStatus;

    public EmployeeState() {
        this.employeeStatus = EmployeeStatus.APPRENTICE;
    }

    protected EmployeeState(boolean jpa) {}

    public void promote() {
        isTrue(employeeStatus == EmployeeStatus.APPRENTICE
                        || employeeStatus == EmployeeStatus.JUNIOR
                        || employeeStatus == EmployeeStatus.SENIOR,
                "Cannot promote: employee must be in APPRENTICE, JUNIOR, or SENIOR status, but was " + employeeStatus);
        this.employeeStatus = switch (employeeStatus) {
            case APPRENTICE -> EmployeeStatus.JUNIOR;
            case JUNIOR -> EmployeeStatus.SENIOR;
            case SENIOR -> EmployeeStatus.MANAGER;
            default -> throw new IllegalStateException("Unexpected status: " + employeeStatus);
        };
    }

    public void terminate() {
        isTrue(employeeStatus == EmployeeStatus.APPRENTICE
                        || employeeStatus == EmployeeStatus.JUNIOR
                        || employeeStatus == EmployeeStatus.SENIOR,
                "Cannot terminate: employee must be in APPRENTICE, JUNIOR, or SENIOR status, but was " + employeeStatus);
        this.employeeStatus = EmployeeStatus.TERMINATED;
    }

    public EmployeeStatus getEmployeeStatus() {
        return employeeStatus;
    }
}
