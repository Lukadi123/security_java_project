package mk.ukim.finki.wp.commonmodel.employee;

import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

public class DataAccessPolicy {

    public static List<String> getAllowedAccess(Employee employee) {
        notNull(employee, "employee must not be null");
        return switch (employee.getEmployeeStatus()) {
            case APPRENTICE -> List.of("public docs", "own profile");
            case JUNIOR -> List.of("project data", "internal tools");
            case SENIOR -> List.of("team data");
            case MANAGER -> List.of("payroll", "HR data");
            default -> List.of();
        };
    }
}
