package mk.ukim.finki.wp.commonmodel.base;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.util.ProxyUtils;

import static org.apache.commons.lang3.Validate.notNull;

@MappedSuperclass
public abstract class AbstractEntity<ID extends DomainObjectId> implements DomainObject {

    @EmbeddedId
    private ID id;

    protected AbstractEntity(ID id) {
        this.id = notNull(id, "id must not be null");
    }

    protected AbstractEntity() {
    }

    public ID getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !getClass().equals(ProxyUtils.getUserClass(obj))) {
            return false;
        }
        var other = (AbstractEntity<?>) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id == null ? super.hashCode() : id.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + "}";
    }
}
