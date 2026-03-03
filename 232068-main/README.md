# DDD Refactoring Project - SDB 2026

## Project Structure
```
src/main/java/mk/ukim/finki/wp/commonmodel/
│
├── base/
│   ├── DomainObject.java
│   ├── ValueObject.java
│   ├── DomainObjectId.java
│   ├── AbstractEntity.java
│   ├── Student.java
│   ├── Professor.java
│   ├── Semester.java
│   └── Subject.java
│
├── valueobjects/
│   ├── StudentSubjectEnrollmentId.java
│   ├── InvalidNote.java
│   ├── NumberOfEnrollments.java
│   ├── GroupName.java
│   ├── GroupId.java
│   ├── ProductId.java
│   ├── Name.java
│   ├── Money.java
│   ├── Quantity.java
│   └── Tag.java
│
├── enrollments/
│   └── StudentSubjectEnrollment.java
│
├── product/
│   └── Product.java
│
└── teachingallocation/
    ├── JoinedSubject.java
    └── schedule/
        └── Course.java
```

---

## Exercise 1: Domain Primitives Pattern

Refactored `StudentSubjectEnrollment` from primitive obsession (`String`, `Short`, `Long`, `Boolean`) to a type-safe domain model with validated value objects.

### Domain Primitives Created

| Class | Before | After |
|---|---|---|
| `StudentSubjectEnrollmentId` | `String id` | UUID, validated, type-safe |
| `InvalidNote` | `String` (0-4000 chars) | 10-4000 chars, character whitelist |
| `NumberOfEnrollments` | `Short` (could be -999) | 1-10 range, domain operations |
| `GroupName` | `String` (SQL injection possible) | Alphanumeric + limited punctuation |
| `GroupId` | `Long` (sequential, enumerable) | UUID-based, non-enumerable |

### Entity Refactoring

| Aspect | Before | After |
|---|---|---|
| Base class | None | `extends AbstractEntity<StudentSubjectEnrollmentId>` |
| ID type | `String id` | UUID |
| Setters | `@Setter` (unrestricted) | Removed — domain methods only |
| Constructor | `@NoArgsConstructor` (public) | Removed — forces valid construction |
| Validation | None | Fail-fast at construction |

### Domain Methods
```java
enrollment.markAsInvalid(new InvalidNote("Missing prerequisite Math101"));
enrollment.markAsValid();
enrollment.recordReEnrollment();
enrollment.assignToGroup(new GroupName("Group A"), GroupId.randomGroupId());
enrollment.removeFromGroup();
boolean canRetry = enrollment.canReEnroll();
```

### Security Improvements

- SQL injection prevented via character whitelisting
- Enumeration attacks prevented via UUID-based IDs
- Invalid business data prevented via fail-fast validation
- Type confusion prevented via distinct strongly-typed classes

---

## Exercise 2: Ensuring Integrity of State

Built a `Product` entity from scratch across 6 commits, progressively applying techniques for safe state management.

### Commit 1: Required Constructor + Optional Fields

- Required-argument constructor — `name`, `price`, `quantity` can never be null on creation
- Protected no-arg constructor for JPA only
- Optional `dateOfProduction` settable after construction, validated to not be in the future

### Commit 2: Separate Update and Clear Methods

- `updateProductDateOfProduction()` — strictly an update, rejects null
- `clearDateOfProduction()` — explicit domain action for clearing
- `updateProductName()` — update name after construction
- Each method has exactly one responsibility

### Commit 3: Fluent Interface

- `withDateOfProduction()` and `withDateOfExpiry()` return `this` for chaining
- Construction reads like natural language:
```java
Product product = new Product(name, price, quantity)
        .withDateOfProduction(productionDate)
        .withDateOfExpiry(expirationDate);
```

### Commit 4: Cross-Field Invariants

- `dateOfExpiry` added as second optional date field
- `checkInvariants()` enforces: if both dates set, production must be before expiry
- Every public mutating method calls `checkInvariants()` before returning
- `clearDateOfProduction()` also clears `dateOfExpiry` — semantically linked
```java
private void checkInvariants() {
    validState((dateOfProduction == null && dateOfExpiry == null)
                    || (dateOfProduction != null && dateOfExpiry == null)
                    || (dateOfProduction != null && dateOfProduction.isBefore(dateOfExpiry)),
            "dateOfProduction must be before dateOfExpiry");
}
```

### Commit 5: Builder Pattern

- Static inner `Builder` class hides the half-built product
- `checkInvariants()` called once in `build()`, not after every step
- Builder self-destructs after `build()` — cannot hand out the same product twice
```java
Product product = new Product.Builder(name, price, quantity)
        .withDateOfProduction(productionDate)
        .withDateOfExpiry(expiryDate)
        .build();
```

### Commit 6: Securing Collections

- `Tag` value object — immutable, `final` field, no setters
- `tags()` returns `Collections.unmodifiableList()` — prevents structural modification
- `getTags()` kept for JPA compatibility
- List initialized to `new ArrayList<>()` in constructor — never null

| Threat | Defense |
|---|---|
| Caller replaces the whole list | Field is private — no setter |
| Caller adds/removes via returned reference | `Collections.unmodifiableList()` |
| Caller modifies items inside the list | `Tag` is immutable |
| List is null on a new product | Initialized in constructor |

---

## Key Principles

- **"If it's not valid, it can't exist"** — validation at construction time
- **Fail Fast** — invalid data rejected immediately
- **Validation Order** — null check → length check → regex (cheap to expensive)
- **Immutability** — value objects never change, operations return new instances
- **Domain Methods over Setters** — `markAsInvalid(note)` not `setValid(false)`
- **Partially Immutable Entities** — `id` is final, other fields change via domain methods

---

## Learning Resources

- *Secure by Design* — Dan Bergh Johnsson, Daniel Deogun, Daniel Sawano
- *Domain-Driven Design* — Eric Evans
- OWASP Top 10 — Injection attacks