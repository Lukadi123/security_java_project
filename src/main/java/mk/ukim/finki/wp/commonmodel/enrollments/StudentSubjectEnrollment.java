package mk.ukim.finki.wp.commonmodel.enrollments;

// CHANGE 1: New imports
// - Removed: @Id, @Column, @Setter, @NoArgsConstructor (no longer needed)
// - Added: @Embedded, @AttributeOverride for domain primitives
// - Added: AbstractEntity (our DDD base class)
// - Added: All domain primitive types
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.ToString;
import mk.ukim.finki.wp.commonmodel.base.AbstractEntity;
import mk.ukim.finki.wp.commonmodel.base.Professor;
import mk.ukim.finki.wp.commonmodel.base.Semester;
import mk.ukim.finki.wp.commonmodel.base.Student;
import mk.ukim.finki.wp.commonmodel.base.Subject;
import mk.ukim.finki.wp.commonmodel.teachingallocation.JoinedSubject;
import mk.ukim.finki.wp.commonmodel.teachingallocation.schedule.Course;
import mk.ukim.finki.wp.commonmodel.valueobjects.*;

import static org.apache.commons.lang3.Validate.notNull;

// CHANGE 2: Removed @Setter — no more uncontrolled mutations
// WHY: @Setter generates setNumEnrollments(Short), setGroupName(String), etc.
//       Anyone can call enrollment.setNumEnrollments((short)-999) with no validation.
//       Instead, we'll have domain methods like markAsInvalid(), recordReEnrollment()

// CHANGE 3: Removed @NoArgsConstructor — forces valid construction
// WHY: @NoArgsConstructor lets anyone write: new StudentSubjectEnrollment()
//       This creates an enrollment with NO semester, NO student, NO subject — invalid!
//       We keep a protected no-arg constructor for JPA only.

// CHANGE 4: @ToString excludes @ManyToOne fields
// WHY: Lombok's @ToString calls toString() on ALL fields, including semester, student, etc.
//       These are @ManyToOne (lazy-loaded). Calling toString() outside a transaction
//       triggers LazyInitializationException. Excluding them prevents this.

@Getter
@ToString(exclude = {"semester", "student", "subject", "joinedSubject", "course", "professor"})
@Entity
// CHANGE 5: extends AbstractEntity<StudentSubjectEnrollmentId>
// BEFORE: public class StudentSubjectEnrollment {  (plain class, @Id String id)
// AFTER:  extends AbstractEntity which provides:
//   - @EmbeddedId of type StudentSubjectEnrollmentId (UUID-based, validated)
//   - equals()/hashCode() based on ID only (entity identity rule)
//   - protected no-arg constructor for JPA
public class StudentSubjectEnrollment extends AbstractEntity<StudentSubjectEnrollmentId> {

    // ============================================================
    // RELATIONSHIPS (unchanged - these are already domain entities)
    // ============================================================
    @ManyToOne
    private Semester semester;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Subject subject;

    // ============================================================
    // CHANGE 6: Raw primitives → Domain primitives
    // Each @Embedded field is a validated value object.
    // @AttributeOverride maps the internal field name to a specific DB column
    // to avoid column name conflicts.
    // ============================================================

    // BEFORE: @Column(length = 4000) private String invalidNote;
    // AFTER:  Validated 10-4000 chars, character whitelist, no SQL injection
    @Embedded
    @AttributeOverride(name = "note", column = @Column(name = "invalid_note"))
    private InvalidNote invalidNote;

    // BEFORE: private Short numEnrollments;  (could be -999, 0, 32767)
    // AFTER:  Validated 1-10, with increment() and canEnrollAgain()
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "num_enrollments"))
    private NumberOfEnrollments numEnrollments;

    // BEFORE: private String groupName;  (any string, SQL injection possible)
    // AFTER:  Validated 1-50 chars, alphanumeric + limited punctuation
    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "group_name"))
    private GroupName groupName;

    // BEFORE: private Long groupId;  (sequential: 1, 2, 3... enumerable)
    // AFTER:  UUID-based, format validated, type-safe
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "group_id"))
    private GroupId groupId;

    // Boolean valid — KEPT AS IS
    // WHY: Boolean is already semantic (true/false).
    // Wrapping it in a ValidityStatus class would add complexity with little benefit.
    private Boolean valid;

    // ============================================================
    // RELATIONSHIPS (continued)
    // ============================================================
    @ManyToOne
    private JoinedSubject joinedSubject;

    @ManyToOne
    private Course course;

    // DEPRECATED FIELDS (kept for backward compatibility)
    @Deprecated
    @ManyToOne
    private Professor professor;

    @Deprecated
    private String professors;

    @Deprecated
    private String assistants;

    // ============================================================
    // CHANGE 7: Constructor — validated, generates UUID
    // ============================================================

    // BEFORE:
    //   public StudentSubjectEnrollment(Semester s, Student st, Subject su) {
    //       this.id = String.format("%s-%s-%s", s.getCode(), st.getIndex(), su.getId());
    //       // no validation, predictable ID
    //   }

    // AFTER:
    //   - Calls super(new StudentSubjectEnrollmentId()) → generates random UUID
    //   - notNull() checks on all required fields
    //   - Initializes valid=true and numEnrollments=1 (sensible defaults)
    public StudentSubjectEnrollment(Semester semester, Student student, Subject subject) {
        super(new StudentSubjectEnrollmentId()); // Generate unpredictable UUID

        this.semester = notNull(semester, "semester must not be null");
        this.student = notNull(student, "student must not be null");
        this.subject = notNull(subject, "subject must not be null");

        // Sensible defaults: enrollment starts as valid, first attempt
        this.valid = true;
        this.numEnrollments = new NumberOfEnrollments(1);
    }

    // CHANGE 8: Protected no-arg constructor for JPA only
    // BEFORE: @NoArgsConstructor (public — anyone can create empty enrollment)
    // AFTER:  protected — only JPA can use it via reflection
    protected StudentSubjectEnrollment() {
        super();
    }

    // ============================================================
    // CHANGE 9: Domain methods replace generic setters
    // ============================================================
    // BEFORE: enrollment.setValid(false); enrollment.setInvalidNote("reason");
    //         (two separate calls that could get out of sync)
    // AFTER:  enrollment.markAsInvalid(new InvalidNote("reason"));
    //         (one atomic operation, note is validated, valid is set automatically)

    /**
     * Mark this enrollment as invalid with a reason.
     *
     * BUSINESS RULE: Requires a meaningful note (10+ chars, validated characters).
     *
     * WHY THIS IS BETTER THAN setValid(false) + setInvalidNote("reason"):
     * 1. Atomic — both fields change together, can't get out of sync
     * 2. Validated — the InvalidNote constructor validates length + characters
     * 3. Expressive — "markAsInvalid" communicates intent, "setValid" doesn't
     */
    public void markAsInvalid(InvalidNote note) {
        this.invalidNote = notNull(note, "invalidNote must not be null when marking as invalid");
        this.valid = false;
    }

    /**
     * Mark enrollment as valid and clear the invalid note.
     *
     * WHY clear the note? If the enrollment is valid, having an "invalid note"
     * attached is contradictory. The domain method keeps the state consistent.
     */
    public void markAsValid() {
        this.valid = true;
        this.invalidNote = null;
    }

    /**
     * Record a re-enrollment attempt.
     *
     * BUSINESS RULE: Max 10 attempts. Throws if already at maximum.
     *
     * WHY NOT enrollment.setNumEnrollments((short)(enrollment.getNumEnrollments() + 1))?
     * 1. That scattered pattern forgets the max check half the time
     * 2. NumberOfEnrollments.increment() enforces the limit internally
     * 3. Returns a new immutable instance (value object pattern)
     */
    public void recordReEnrollment() {
        this.numEnrollments = this.numEnrollments.increment();
    }

    /**
     * Assign student to a group.
     *
     * WHY both groupName AND groupId together?
     * A group assignment requires both pieces of info.
     * With setters, someone could set groupName but forget groupId.
     * This method requires both, ensuring consistent state.
     */
    public void assignToGroup(GroupName groupName, GroupId groupId) {
        this.groupName = notNull(groupName, "groupName must not be null");
        this.groupId = notNull(groupId, "groupId must not be null");
    }

    /**
     * Remove student from group.
     * Clears BOTH groupName and groupId together (consistent state).
     */
    public void removeFromGroup() {
        this.groupName = null;
        this.groupId = null;
    }

    /**
     * Assign a course to this enrollment.
     */
    public void assignCourse(Course course) {
        this.course = notNull(course, "course must not be null");
    }

    /**
     * Assign a joined subject (for merged lectures).
     */
    public void assignJoinedSubject(JoinedSubject joinedSubject) {
        this.joinedSubject = notNull(joinedSubject, "joinedSubject must not be null");
    }

    /**
     * Check if student can re-enroll.
     * Delegates to NumberOfEnrollments — the business rule lives in the domain primitive.
     */
    public boolean canReEnroll() {
        return this.numEnrollments.canEnrollAgain();
    }

    /**
     * Check if enrollment is currently valid.
     */
    public Boolean isValid() {
        return valid;
    }
}
