package mk.ukim.finki.wp.commonmodel.base;

import jakarta.persistence.Embeddable;
import jakarta.persistence.MappedSuperclass;

import java.util.Objects;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.matchesPattern;
import static org.apache.commons.lang3.Validate.notNull;

@MappedSuperclass
@Embeddable
public class DomainObjectId implements ValueObject {

    private String id;

    protected DomainObjectId(String uuid) {
        notNull(uuid, "uuid must not be null");
        matchesPattern(uuid,
                "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}",
                "The UUID must be in a valid format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
        this.id = uuid;
    }

    protected DomainObjectId() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainObjectId that = (DomainObjectId) o;
        return Objects.equals(id, that.id);
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
