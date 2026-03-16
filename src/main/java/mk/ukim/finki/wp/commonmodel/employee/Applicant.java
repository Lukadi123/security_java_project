package mk.ukim.finki.wp.commonmodel.employee;

import jakarta.persistence.*;
import lombok.Getter;
import mk.ukim.finki.wp.commonmodel.base.AbstractEntity;
import mk.ukim.finki.wp.commonmodel.valueobjects.ApplicantId;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

@Getter
@Entity
public class Applicant extends AbstractEntity<ApplicantId> {

    @Enumerated(EnumType.STRING)
    private ApplicantStatus status;

    private String name;

    public Applicant(String name) {
        super(new ApplicantId());
        notNull(name, "name must not be null");
        this.name = name;
        this.status = ApplicantStatus.APPLICANT;
    }

    protected Applicant() {
        super();
    }

    public Employee approve() {
        isTrue(status == ApplicantStatus.APPLICANT,
                "Cannot approve: applicant must be in APPLICANT status, but was " + status);
        this.status = ApplicantStatus.APPROVED;
        return new Employee(this.name);
    }

    public void reject() {
        isTrue(status == ApplicantStatus.APPLICANT,
                "Cannot reject: applicant must be in APPLICANT status, but was " + status);
        this.status = ApplicantStatus.REJECTED;
    }
}
