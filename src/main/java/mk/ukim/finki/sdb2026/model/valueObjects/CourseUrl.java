package mk.ukim.finki.sdb2026.model.valueObjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;
import static org.apache.commons.lang3.Validate.isTrue;

import static org.apache.commons.lang3.Validate.matchesPattern;
import static org.apache.commons.lang3.Validate.notNull;

@Embeddable
@Getter
public class CourseUrl implements ValueObject {

    private final String url;

    protected CourseUrl() {
        this.url = null;
    }

    public CourseUrl(String url) {
        notNull(url, "CourseUrl must not be null");
        isTrue(url.length() >= 10 && url.length() <= 200,
                "CourseUrl must be 10-200 characters long");
        matchesPattern(url, "^https://[A-Za-z0-9.\\-/_?=&:]{2,192}$",
                "CourseUrl must start with https:// and contain only letters, digits, " +
                        "and the following special characters: . - / _ ? = & :");
        this.url = url;
    }


    @Override
    public String toString() { return url; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return this.url.equals(((CourseUrl) obj).url);
    }

    @Override
    public int hashCode() { return url.hashCode(); }
}
