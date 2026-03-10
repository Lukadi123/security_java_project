package mk.ukim.finki.wp.commonmodel.valueobjects;

import jakarta.persistence.Embeddable;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.*;

@Embeddable
public class InvalidNote implements ValueObject {

    private String note;

    public InvalidNote(String note) {
        notNull(note, "InvalidNote must not be null");
        inclusiveBetween(10, 4000, note.length(),
                "InvalidNote must be between 10 and 4000 characters, got: " + note.length());
        matchesPattern(note, "^[A-Za-z0-9\\s.,:!?'\"()\\-/\\n\\r]+$",
                "InvalidNote contains invalid characters. Allowed: letters, digits, spaces, punctuation (.,:!?'\"()-/)");
        this.note = note;
    }

    protected InvalidNote() {
        this.note = null;
    }

    public String getNote() {
        return note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvalidNote that = (InvalidNote) o;
        return Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(note);
    }

    @Override
    public String toString() {
        return note;
    }
}
