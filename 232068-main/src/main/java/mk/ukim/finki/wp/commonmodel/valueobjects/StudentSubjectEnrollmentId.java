package mk.ukim.finki.wp.commonmodel.valueobjects;

import mk.ukim.finki.wp.commonmodel.base.DomainObjectId;

/**
 * Strongly-typed ID for StudentSubjectEnrollment.
 *
 * BEFORE (vulnerable):
 * - String id = "anything goes!"
 * - No validation
 * - Can be empty, null, malformed
 *
 * AFTER (secure):
 * - UUID format enforced
 * - Cannot accidentally pass a StudentId where EnrollmentId is expected
 * - Type system prevents mistakes
 *
 * SECURITY BENEFIT:
 * - Non-sequential: /api/enrollments/550e8400-e29b-41d4-a716-446655440000
 * - Attacker cannot enumerate IDs by incrementing
 *
 * TYPE SAFETY:
 * - Cannot pass wrong ID type:
 *   deleteEnrollment(StudentId) ❌ compile error
 *   deleteEnrollment(StudentSubjectEnrollmentId) ✅ correct
 */
public class StudentSubjectEnrollmentId extends DomainObjectId {

    /**
     * Restore an existing ID from a string (e.g., from database).
     */
    public StudentSubjectEnrollmentId(String id) {
        super(id);
    }

    /**
     * Generate a new random UUID.
     * Called when creating a new enrollment.
     */
    public StudentSubjectEnrollmentId() {
        super();
    }
}
