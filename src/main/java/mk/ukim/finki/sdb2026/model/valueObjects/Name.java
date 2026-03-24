package mk.ukim.finki.sdb2026.model.valueObjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;

import static org.apache.commons.lang3.Validate.matchesPattern;
import static org.apache.commons.lang3.Validate.notNull;

@Embeddable
@Getter
public class Name implements ValueObject {

    private final String name;

    protected Name() {
        this.name = null;
    }

    public Name(String name) {
        notNull(name, "name must not be null");
        matchesPattern(name, "^[A-Za-z0-9'\\-()., ]{2,100}$",
                "name must be 2-100 characters long and contain only letters, digits, " +
                        "spaces, and the following special characters: ' - ( ) . ,");
        this.name = name;
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return this.name.equals(((Name) obj).name);
    }

    @Override
    public int hashCode() { return name.hashCode(); }
}