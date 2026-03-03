# Exercise 1 - From CRUD to Secure Domain Model (StudentSubjectEnrollment)

## What This Exercise Does

We refactored `StudentSubjectEnrollment` from a class full of raw primitives (`String`, `Short`, `Long`) into a secure domain model using **Domain-Driven Design (DDD)** and the **Domain Primitives** pattern from "Secure by Design".

---

## The Problem: Primitive Obsession

The original class accepted any value with no validation:

```java
@Getter @Setter @NoArgsConstructor @Entity
public class StudentSubjectEnrollment {
    @Id private String id;                    // any string
    private String invalidNote;               // SQL injection possible
    private Short numEnrollments;             // -999? 32767?
    private String groupName;                 // "'; DROP TABLE--"
    private Long groupId;                     // 1, 2, 3... enumerable
}
```

**What could go wrong:**
- `enrollment.setNumEnrollments((short) -999)` -- compiles, runs, makes no sense
- `enrollment.setGroupName("'; DROP TABLE students;--")` -- SQL injection payload accepted
- An attacker trying `/api/enrollments/1`, `/2`, `/3`... can enumerate all records
- `new StudentSubjectEnrollment()` creates an empty, invalid enrollment

---

## Step 1: DDD Base Classes (Already Provided)

| Class | Purpose |
|---|---|
| `DomainObject` | Marker interface. Tags classes as domain citizens. Extends `Serializable`. |
| `ValueObject` | Marker for immutable value objects (compared by value, not identity). |
| `DomainObjectId` | UUID-based ID base class. Validates format. Prevents enumeration attacks. |
| `AbstractEntity` | Entity base class. ID-based equality. `@EmbeddedId` support. |

**Key concepts:**
- **Entities** = identity (same ID = same thing, even if attributes differ)
- **Value Objects** = no identity (same attributes = same thing)
- **UUIDs** prevent enumeration attacks (unpredictable, 2^122 possibilities)

---

## Step 2: Domain Primitives Created

### The Rule: "If it's not valid, it can't exist"

Each domain primitive validates at construction time. If validation fails, the object is never created.

### Validation Order (cheap to expensive):
1. **Null check** -- O(1), cheapest
2. **Size/range check** -- O(1)
3. **Regex pattern** -- O(n), most expensive, do last

### Domain Primitives Created:

| Primitive | Replaces | Validation | Why |
|---|---|---|---|
| `StudentSubjectEnrollmentId` | `String id` | UUID format | Prevents enumeration attacks, type-safe |
| `InvalidNote` | `String invalidNote` | 10-4000 chars, character whitelist | Prevents SQL injection, forces meaningful notes |
| `NumberOfEnrollments` | `Short numEnrollments` | 1-10 range, `increment()`, `canEnrollAgain()` | Business rule enforcement, domain operations |
| `GroupName` | `String groupName` | 1-50 chars, `[A-Za-z0-9 .\-/]` | Prevents SQL injection, length limits |
| `GroupId` | `Long groupId` | UUID format | Prevents enumeration, type-safe |

### What Was Kept As-Is:
- `Boolean valid` -- Already semantic (true/false). Wrapping adds little value.
- `@ManyToOne` relationships -- Already domain entities.

---

## Step 3: Entity Refactoring

### Changes Made:

| What | Before | After | Why |
|---|---|---|---|
| Base class | None | `extends AbstractEntity<StudentSubjectEnrollmentId>` | UUID ID, entity equality |
| `@Setter` | Present | Removed | Prevents uncontrolled mutations |
| `@NoArgsConstructor` | Public | Protected (JPA only) | Forces valid construction |
| `@ToString` | All fields | Excludes `@ManyToOne` | Prevents `LazyInitializationException` |
| ID | `String` (predictable) | `StudentSubjectEnrollmentId` (UUID) | Prevents enumeration |
| Raw fields | `String`, `Short`, `Long` | Domain primitives | Validated, type-safe |
| Mutations | Generic setters | Domain methods | Expressive, atomic, validated |

### Domain Methods Created:

```
markAsInvalid(InvalidNote)  -- sets valid=false AND note together (atomic)
markAsValid()               -- sets valid=true AND clears note (consistent)
recordReEnrollment()        -- increments count, throws at max 10
assignToGroup(GroupName, GroupId) -- sets both together (consistent)
removeFromGroup()           -- clears both together (consistent)
canReEnroll()               -- delegates to NumberOfEnrollments
```

### Why Domain Methods > Setters:

**With setters (dangerous):**
```java
enrollment.setValid(false);           // step 1
enrollment.setInvalidNote("reason");  // step 2 -- easy to forget!
// What if step 2 is forgotten? valid=false with no reason.
```

**With domain methods (safe):**
```java
enrollment.markAsInvalid(new InvalidNote("Student missing Math101"));
// Both fields set together. Note is validated. Can't forget anything.
```

---

## @AttributeOverride -- Why It's Needed

When using `@Embedded` domain primitives, JPA maps their internal fields to DB columns.
Problem: `GroupId` has a field called `id` -- this clashes with the entity's own `id` column.

Solution: `@AttributeOverride` maps each embedded field to a specific column name:

```java
@Embedded
@AttributeOverride(name = "id", column = @Column(name = "group_id"))
private GroupId groupId;
```

---

## Security Improvements Summary

1. **SQL Injection** -- Character whitelisting in `GroupName` and `InvalidNote` prevents injection payloads. JPA parameterized queries are the primary defense; domain primitives are defense-in-depth.

2. **Enumeration Attacks** -- UUID-based IDs (2^122 possibilities) replace sequential `Long` IDs. Attacker cannot guess `/api/enrollments/2` after seeing `/1`.

3. **Invalid Business Data** -- `NumberOfEnrollments(-5)` throws. `new GroupName("")` throws. `new InvalidNote("x")` throws. Invalid data cannot enter the system.

4. **Type Safety** -- Cannot pass `GroupId` where `StudentSubjectEnrollmentId` is expected. The compiler catches mistakes that raw `String`/`Long` would miss.

5. **Consistent State** -- Domain methods like `markAsInvalid()` change related fields together atomically. No more "valid=false but no reason attached" inconsistencies.

---

## Key Principles from "Secure by Design"

1. **"If it's not valid, it can't exist"** -- Domain primitives reject bad data at construction
2. **Fail Fast** -- Validation at the boundary (construction), not scattered in services
3. **Immutability** -- Value objects return new instances (`increment()` returns new `NumberOfEnrollments`)
4. **Domain Methods > Setters** -- `markAsInvalid(reason)` instead of `setValid(false)`
5. **Character Whitelisting** -- Allow only known-good characters, reject everything else
6. **Non-Enumerable IDs** -- UUIDs instead of sequential integers
