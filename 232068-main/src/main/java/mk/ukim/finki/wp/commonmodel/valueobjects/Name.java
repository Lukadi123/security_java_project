package mk.ukim.finki.wp.commonmodel.valueobjects;

import jakarta.persistence.Embeddable;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;
import java.util.Objects;
import static org.apache.commons.lang3.Validate.*;

@Embeddable
public class Name implements ValueObject {
    private String name;

    public Name(String name) {
        notNull(name, "Name must not be null");
        inclusiveBetween(1, 100, name.length(), "Name must be between 1 and 100 characters");
        this.name = name;
    }

    protected Name() {}

    public String getName() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(name, ((Name) o).name);
    }

    @Override
    public int hashCode() { return Objects.hash(name); }

    @Override
    public String toString() { return name; }
}