package mk.ukim.finki.wp.commonmodel.valueobjects;


import jakarta.persistence.Embeddable;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;
import java.util.Objects;
import static org.apache.commons.lang3.Validate.*;

@Embeddable
public class Tag implements ValueObject {
    private final String tagName;

    public Tag(String tagName) {
        notNull(tagName, "Tag name must not be null");
        inclusiveBetween(1, 50, tagName.length(), "Tag name must be between 1 and 50 characters");
        this.tagName = tagName;
    }

    protected Tag() {
        this.tagName = null;
    }

    public String getTagName() {
        return tagName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(tagName, ((Tag) o).tagName);
    }

    @Override
    public int hashCode() { return Objects.hash(tagName); }

    @Override
    public String toString() { return tagName; }
}