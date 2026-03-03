package mk.ukim.finki.wp.commonmodel.valueobjects;

import jakarta.persistence.Embeddable;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.*;

/**
 * Value object for enrollment invalidation notes.
 *
 * BEFORE (vulnerable):
 * - String invalidNote (length = 4000)
 * - Could be empty, null, contain SQL injection
 * - No minimum length check
 * - No character validation
 *
 * AFTER (secure):
 * - 10-4000 characters (meaningful notes only)
 * - Character whitelist (prevents injection)
 * - Cannot construct invalid note
 *
 * VALIDATION ORDER (cheap to expensive):
 * 1. Null check ← cheapest
 * 2. Length check ← cheap
 * 3. Regex pattern ← expensive (last)
 *
 * BUSINESS RULE:
 * - Notes must be meaningful (min 10 chars)
 * - Example: ❌ "Bad" (too short)
 * - Example: ✅ "Student has not completed prerequisite course Math101"
 */
@Embeddable
public class InvalidNote implements ValueObject {

    private String note;

    /**
     * Creates a validated InvalidNote.
     *
     * @param note The note explaining why enrollment is invalid
     * @throws NullPointerException if note is null
     * @throws IllegalArgumentException if note is too short/long or has invalid characters
     */
    public InvalidNote(String note) {
        // 1. Null check (cheapest)
        notNull(note, "InvalidNote must not be null");

        // 2. Size check (cheap)
        inclusiveBetween(10, 4000, note.length(),
                "InvalidNote must be between 10 and 4000 characters, got: " + note.length());

        // 3. Lexical content (expensive - regex last)
        // Allow letters, numbers, spaces, common punctuation, newlines
        matchesPattern(note, "^[A-Za-z0-9\\s.,:!?'\"()\\-/\\n\\r]+$",
                "InvalidNote contains invalid characters. Allowed: letters, digits, spaces, punctuation (.,:!?'\"()-/)");

        this.note = note;
    }

    /**
     * JPA requires a no-arg constructor for @Embeddable.
     * Protected so application code cannot bypass validation.
     */
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
