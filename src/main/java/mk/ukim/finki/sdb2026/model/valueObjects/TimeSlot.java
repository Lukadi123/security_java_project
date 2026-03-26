package mk.ukim.finki.sdb2026.model.valueObjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;
import static org.apache.commons.lang3.Validate.isTrue;


import static org.apache.commons.lang3.Validate.matchesPattern;
import static org.apache.commons.lang3.Validate.notNull;

@Embeddable
@Getter
public class TimeSlot implements ValueObject {

    private final String timeSlot;

    protected TimeSlot() {
        this.timeSlot = null;
    }

    public TimeSlot(String timeSlot) {
        notNull(timeSlot, "TimeSlot must not be null");
        isTrue(timeSlot.length() >= 5 && timeSlot.length() <= 60,
                "TimeSlot must be 5-60 characters long");
        matchesPattern(timeSlot, "^[A-Za-z0-9 :\\-+&()]{5,60}$",
                "TimeSlot must be 5-60 characters long and contain only letters, digits, " +
                        "spaces, and the following special characters: : - + & ( )");
        this.timeSlot = timeSlot;
    }


    @Override
    public String toString() { return timeSlot; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return this.timeSlot.equals(((TimeSlot) obj).timeSlot);
    }

    @Override
    public int hashCode() { return timeSlot.hashCode(); }
}
