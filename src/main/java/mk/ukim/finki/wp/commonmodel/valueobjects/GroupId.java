package mk.ukim.finki.wp.commonmodel.valueobjects;

import jakarta.persistence.Embeddable;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;

import java.util.Objects;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.*;

@Embeddable
public class GroupId implements ValueObject {

    private String id;

    public GroupId(String id) {
        notNull(id, "GroupId must not be null");
        matchesPattern(id,
                "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}",
                "GroupId must be a valid UUID format");
        this.id = id;
    }

    protected GroupId() {
        this.id = null;
    }

    public static GroupId randomGroupId() {
        return new GroupId(UUID.randomUUID().toString());
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupId groupId = (GroupId) o;
        return Objects.equals(id, groupId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }
}
