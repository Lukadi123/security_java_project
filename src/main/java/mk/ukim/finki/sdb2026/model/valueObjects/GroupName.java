package mk.ukim.finki.sdb2026.model.valueObjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.matchesPattern;
import static org.apache.commons.lang3.Validate.notNull;

@Embeddable
@Getter
public class GroupName implements ValueObject {

    private final String name;

    protected GroupName() {
        this.name = null;
    }

    public GroupName(String name) {
        notNull(name, "GroupName must not be null");
        isTrue(name.length() >= 1 && name.length() <= 50,
                "GroupName must be 1-50 characters long");
        matchesPattern(name, "^[A-Za-z0-9 .\\-/&:+=]{1,50}$",
                "GroupName must be 1-50 characters and contain only letters, digits, " +
                        "spaces, and the following special characters: . - / & : + =");
        this.name = name;
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return this.name.equals(((GroupName) obj).name);
    }

    @Override
    public int hashCode() { return name.hashCode(); }
}