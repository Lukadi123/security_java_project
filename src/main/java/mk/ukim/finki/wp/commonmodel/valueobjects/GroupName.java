package mk.ukim.finki.wp.commonmodel.valueobjects;

import jakarta.persistence.Embeddable;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.*;

@Embeddable
public class GroupName implements ValueObject {

    private String name;

    public GroupName(String name) {
        notNull(name, "GroupName must not be null");
        inclusiveBetween(1, 50, name.length(),
                "GroupName must be between 1 and 50 characters, got: " + name.length());
        matchesPattern(name, "^[A-Za-z0-9 .\\-/]+$",
                "GroupName can only contain letters, digits, spaces, and the following: . - /");
        this.name = name;
    }

    protected GroupName() {
        this.name = null;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupName groupName = (GroupName) o;
        return Objects.equals(name, groupName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
