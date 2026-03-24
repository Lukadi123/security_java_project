package mk.ukim.finki.sdb2026.model.valueObjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;

import java.util.regex.Pattern;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.matchesPattern;
import static org.apache.commons.lang3.Validate.notNull;

@Embeddable
@Getter
public class GroupName implements ValueObject {

    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "(?i).*(" +
                    "\\bor\\b|\\band\\b|\\bselect\\b|\\binsert\\b|\\bupdate\\b|" +
                    "\\bdelete\\b|\\bdrop\\b|\\bunion\\b|\\bexec\\b|\\bwhere\\b|" +
                    "--" +
                    ").*"
    );

    private final String name;

    protected GroupName() {
        this.name = null;
    }

    public GroupName(String name) {
        notNull(name, "GroupName must not be null");
        isTrue(name.trim().length() >= 1,
                "GroupName must not be blank");
        isTrue(name.length() >= 1 && name.length() <= 50,
                "GroupName must be 1-50 characters long");
        matchesPattern(name, "^[A-Za-z0-9 .\\-/&:+=]{1,50}$",
                "GroupName must be 1-50 characters and contain only letters, digits, " +
                        "spaces, and the following special characters: . - / & : + =");
        isTrue(!SQL_INJECTION_PATTERN.matcher(name).matches(),
                "GroupName contains illegal patterns");
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