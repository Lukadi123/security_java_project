package mk.ukim.finki.wp.commonmodel.valueobjects;

import jakarta.persistence.Embeddable;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;
import java.util.Objects;
import static org.apache.commons.lang3.Validate.*;

@Embeddable
public class Quantity implements ValueObject {
    private Integer value;

    public Quantity(Integer value) {
        notNull(value, "Quantity must not be null");
        isTrue(value >= 0, "Quantity must not be negative");
        this.value = value;
    }

    protected Quantity() {}

    public Integer getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(value, ((Quantity) o).value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return value.toString(); }
}