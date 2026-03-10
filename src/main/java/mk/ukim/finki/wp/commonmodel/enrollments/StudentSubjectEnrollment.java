package mk.ukim.finki.wp.commonmodel.enrollments;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.ToString;
import mk.ukim.finki.wp.commonmodel.base.AbstractEntity;
import mk.ukim.finki.wp.commonmodel.base.Semester;
import mk.ukim.finki.wp.commonmodel.base.Student;
import mk.ukim.finki.wp.commonmodel.base.Subject;
import mk.ukim.finki.wp.commonmodel.teachingallocation.JoinedSubject;
import mk.ukim.finki.wp.commonmodel.teachingallocation.schedule.Course;
import mk.ukim.finki.wp.commonmodel.valueobjects.*;

import static org.apache.commons.lang3.Validate.notNull;

@Getter
@ToString(exclude = {"semester", "student", "subject", "joinedSubject", "course"})
@Entity
public class StudentSubjectEnrollment extends AbstractEntity<StudentSubjectEnrollmentId> {

    @ManyToOne
    private Semester semester;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Subject subject;

    @Embedded
    @AttributeOverride(name = "note", column = @Column(name = "invalid_note"))
    private InvalidNote invalidNote;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "num_enrollments"))
    private NumberOfEnrollments numEnrollments;

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "group_name"))
    private GroupName groupName;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "group_id"))
    private GroupId groupId;

    private Boolean valid;

    @ManyToOne
    private JoinedSubject joinedSubject;

    @ManyToOne
    private Course course;

    public StudentSubjectEnrollment(Semester semester, Student student, Subject subject) {
        super(new StudentSubjectEnrollmentId());
        this.semester = notNull(semester, "semester must not be null");
        this.student = notNull(student, "student must not be null");
        this.subject = notNull(subject, "subject must not be null");
        this.valid = true;
        this.numEnrollments = new NumberOfEnrollments(1);
    }

    protected StudentSubjectEnrollment() {
        super();
    }

    public void markAsInvalid(InvalidNote note) {
        this.invalidNote = notNull(note, "invalidNote must not be null when marking as invalid");
        this.valid = false;
    }

    public void markAsValid() {
        this.valid = true;
        this.invalidNote = null;
    }

    public void recordReEnrollment() {
        this.numEnrollments = this.numEnrollments.increment();
    }

    public void assignToGroup(GroupName groupName, GroupId groupId) {
        this.groupName = notNull(groupName, "groupName must not be null");
        this.groupId = notNull(groupId, "groupId must not be null");
    }

    public void removeFromGroup() {
        this.groupName = null;
        this.groupId = null;
    }

    public void assignCourse(Course course) {
        this.course = notNull(course, "course must not be null");
    }

    public void assignJoinedSubject(JoinedSubject joinedSubject) {
        this.joinedSubject = notNull(joinedSubject, "joinedSubject must not be null");
    }

    public boolean canReEnroll() {
        return this.numEnrollments.canEnrollAgain();
    }

    public Boolean isValid() {
        return valid;
    }
}
