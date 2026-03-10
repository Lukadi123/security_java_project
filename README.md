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
│   ├── OrderId.java
│   ├── ShoppingCartId.java
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
├── order/
│   ├── OrderStatus.java
│   ├── OrderState.java
│   ├── Order.java
│   ├── ShoppingCartStatus.java
│   └── ShoppingCart.java
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
| `InvalidNote` | `String` (0–4000 chars) | 10–4000 chars, character whitelist |
| `NumberOfEnrollments` | `Short` (could be -999) | 1–10 range, domain operations |
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
| Caller replaces the whole list | Field is `private` — no setter |
| Caller adds/removes via returned reference | `Collections.unmodifiableList()` |
| Caller modifies items inside the list | `Tag` is immutable |
| List is null on a new product | Initialized in constructor |

---

## Exercise 3: Reducing Complexity of State

Introduced the `Order` aggregate and `ShoppingCart` aggregate, progressively applying three patterns for managing complex entity state.

### Commit 1: Order Aggregate with State-Guarded Methods

Introduced `Order` with a full lifecycle defined in `OrderStatus`:

| State | Meaning |
|---|---|
| `CREATED` | Order just created; products can still be added/removed |
| `SUBMITTED` | Customer has finalized; no more product changes |
| `PROCESSED` | Order accepted and being prepared |
| `IN_TRANSPORT` | Order is out for delivery |
| `DELIVERED` | Order successfully delivered (requires payment) |
| `CANCELED` | Order was cancelled before submission |

Every mutating method guards its precondition with `isTrue()` before applying any change:

- `addProduct / removeProduct / clearProducts` — only in `CREATED`
- `cancel()` — only from `CREATED`
- `submit()` — only from `CREATED`, requires at least one product
- `process()` — only from `SUBMITTED`
- `beginTransport()` — only from `PROCESSED`
- `pay()` — from `PROCESSED` or `IN_TRANSPORT` (payment is orthogonal to transport)
- `deliver()` — only from `IN_TRANSPORT` and only if `isPaid = true`

All constraints live inside the aggregate — no service layer needed to enforce them.

### Commit 2: Dedicated `OrderState` Value Object

Extracted state logic from `Order` into a separate `@Embeddable` `OrderState` class.

`OrderState` owns two fields together: `orderStatus` + `isPaid`. These belong together because their combined value determines what is allowed — keeping them in the same object makes that relationship explicit.

`Order` methods become thin wrappers that delegate to `OrderState`:

```java
public void cancel() { state.cancel(); }
public void submit() { state.submit(!products.isEmpty()); }
```

**Benefits:**
- **Single responsibility** — `OrderState` manages transitions, `Order` manages data
- **Cohesion** — `orderStatus` and `isPaid` can never drift apart
- **Testability** — `OrderState` can be unit-tested without constructing a full `Order`

### Commit 3: Entity Relay — ShoppingCart + Order

Split the `Order` lifecycle into two focused aggregates:

**`ShoppingCart`** handles the first leg (pre-submission):
- Starts in `CREATED`
- Manages product list: `addProduct()`, `removeProduct()`, `clearProducts()`
- `cancel()` — only from `CREATED`
- `submit()` — validates cart has products, marks cart `SUBMITTED`, creates and returns a new `Order`

**`Order`** handles the second leg (post-submission):
- `CREATED` removed from `OrderStatus` entirely
- Born only from `ShoppingCart.submit()` — constructor is `protected`
- Starts directly in `SUBMITTED` — product list is locked in at creation
- Handles: `cancel()`, `process()`, `pay()`, `beginTransport()`, `deliver()`

**The relay:**

```
ShoppingCart:  CREATED → SUBMITTED
                       ↘ CANCELED
                            ↓ submit() creates an Order

Order:         SUBMITTED → PROCESSED → IN_TRANSPORT → DELIVERED
                       ↘ CANCELED        ↑ pay() anytime from PROCESSED
```

**When to apply Entity Relay:**
- State graph has grown too large to audit safely
- There is a clear cut point with no loopbacks between phases
- A single event triggers the handoff (here: submitting the cart)

### Security Takeaways

State management is a security concern — every gap in an entity's state model is a potential attack vector:

- An order modifiable after payment → attacker receives goods without paying
- An order shippable without payment → direct financial loophole
- State checks scattered across services → inconsistency exploitable over time

All three patterns serve the same goal: **make invalid states unrepresentable and valid transitions explicit, auditable, and impossible to bypass**.

---

## Key Principles Applied (All Exercises)

- **"If it's not valid, it can't exist"** — validation at construction time
- **Fail Fast** — invalid data rejected immediately at the boundary
- **Validation Order** — null check → length check → regex (cheap to expensive)
- **Immutability** — value objects never change, operations return new instances
- **Domain Methods over Setters** — `markAsInvalid(note)` not `setValid(false)`
- **Constraints inside the aggregate** — not in service layers or controllers

## Learning Resources

- *Secure by Design* — Dan Bergh Johnsson, Daniel Deogun, Daniel Sawano
- *Domain-Driven Design* — Eric Evans
- OWASP Top 10 — Injection attacks
# DDD Refactoring Project - StudentSubjectEnrollment

## 🎯 Project Overview

This project demonstrates **refactoring a Spring Boot entity using Domain-Driven Design (DDD) principles** and the **Domain Primitives pattern** from the book "Secure by Design".

**Original class:** Primitive obsession (String, Short, Long, Boolean)
**Refactored class:** Type-safe domain model with validated value objects

---

## 📁 Project Structure

```
ddd-refactoring-project/
└── src/main/java/mk/ukim/finki/wp/commonmodel/
    │
    ├── 📦 base/                           (Step 1: DDD Base Classes)
    │   ├── DomainObject.java              ← Marker interface
    │   ├── ValueObject.java               ← Marker for value objects
    │   ├── DomainObjectId.java            ← UUID-based ID base class
    │   └── AbstractEntity.java            ← Entity base class
    │
    ├── 💎 valueobjects/                   (Step 2: Domain Primitives)
    │   ├── StudentSubjectEnrollmentId.java   ← UUID ID (non-enumerable)
    │   ├── InvalidNote.java                  ← 10-4000 chars, validated
    │   ├── NumberOfEnrollments.java          ← 1-10 range, domain operations
    │   ├── GroupName.java                    ← 1-50 chars, SQL-safe
    │   └── GroupId.java                      ← UUID, type-safe
    │
    └── 🏛️ enrollments/                    (Step 3: Refactored Entity)
        └── StudentSubjectEnrollment.java     ← DDD entity with domain primitives
```

---

## 🔄 Refactoring Steps Applied

### Step 1: Create DDD Base Classes ✅

**Files created:**
- `DomainObject.java` - Marker interface for all domain objects
- `ValueObject.java` - Marker for immutable value objects
- `DomainObjectId.java` - UUID-based ID with format validation
- `AbstractEntity.java` - Entity base with identity-based equality

**Key concepts:**
- **Entities** have identity (same ID = same object)
- **Value Objects** have no identity (same attributes = same object)
- **Immutability** - Value objects never change, return new instances

---

### Step 2: Create Domain Primitives ✅

**Files created:**

#### `StudentSubjectEnrollmentId.java`
```java
// BEFORE: String id = "anything"
// AFTER:  StudentSubjectEnrollmentId id = new StudentSubjectEnrollmentId()
```
- UUID-based (non-enumerable, secure)
- Format validated (must match UUID pattern)
- Type-safe (can't mix with StudentId or GroupId)

#### `InvalidNote.java`
```java
// BEFORE: String invalidNote (0-4000 chars, no validation)
// AFTER:  InvalidNote note = new InvalidNote("Detailed reason...")
```
- 10-4000 characters (meaningful notes only)
- Character whitelist (prevents SQL injection)
- Null checks + length checks + regex pattern

#### `NumberOfEnrollments.java`
```java
// BEFORE: Short numEnrollments (could be -999 or 32,767)
// AFTER:  NumberOfEnrollments num = new NumberOfEnrollments(5)
```
- 1-10 range (business rule: max 10 re-enrollment attempts)
- Domain operations: `increment()`, `canEnrollAgain()`
- Immutable (returns new instance on operations)

#### `GroupName.java`
```java
// BEFORE: String groupName = "'; DROP TABLE--"
// AFTER:  GroupName name = new GroupName("Group A")
```
- 1-50 characters
- Alphanumeric + spaces + limited punctuation (. - /)
- Prevents SQL injection via character whitelist

#### `GroupId.java`
```java
// BEFORE: Long groupId = 1L (sequential, enumerable)
// AFTER:  GroupId id = GroupId.randomGroupId()
```
- UUID-based (non-enumerable)
- Format validated
- Type-safe

---

### Step 3: Refactor Entity ✅

**File refactored:** `StudentSubjectEnrollment.java`

#### Changes Made:

| Aspect | Before | After |
|---|---|---|
| **Base class** | No base class | `extends AbstractEntity<StudentSubjectEnrollmentId>` |
| **ID type** | `String id` | `StudentSubjectEnrollmentId` (UUID) |
| **Setters** | `@Setter` (unrestricted) | ❌ Removed - domain methods only |
| **Constructor** | `@NoArgsConstructor` | ❌ Removed - forces valid construction |
| **Validation** | None | Fail-fast at construction |
| **Primitives** | `String`, `Short`, `Long`, `Boolean` | Domain primitives (validated) |
| **Operations** | Generic setters | Domain methods (`markAsInvalid`, `recordReEnrollment`, etc.) |

#### Domain Methods Added:

```java
// Instead of generic setters, expressive domain operations:
enrollment.markAsInvalid(new InvalidNote("Missing prerequisite Math101"));
enrollment.markAsValid();
enrollment.recordReEnrollment();
enrollment.assignToGroup(new GroupName("Group A"), GroupId.randomGroupId());
enrollment.removeFromGroup();
boolean canRetry = enrollment.canReEnroll();
```

---

## 🛡️ Security Improvements

### 1. **Prevents SQL Injection**
- ✅ Character whitelisting in `GroupName` and `InvalidNote`
- ✅ Regex validation rejects malicious characters
- ✅ `"'; DROP TABLE--"` cannot be constructed

### 2. **Prevents Enumeration Attacks**
- ✅ UUID-based IDs (2^122 possibilities)
- ✅ `/api/enrollments/1, /2, /3...` → Now unpredictable UUIDs
- ✅ Attacker cannot enumerate all enrollments

### 3. **Prevents Invalid Business Data**
- ✅ Negative enrollments impossible
- ✅ 999 enrollments impossible
- ✅ Empty notes impossible
- ✅ Fail-fast with clear error messages

### 4. **Type Safety**
- ✅ Cannot pass `GroupId` where `StudentSubjectEnrollmentId` expected
- ✅ Compiler catches parameter order mistakes
- ✅ No ambiguous parameter lists

---

## 💡 Key Principles Applied

### 1. **"If it's not valid, it can't exist"**
```java
// ❌ BEFORE: Invalid data accepted
enrollment.setNumEnrollments((short) -999);  // compiles!

// ✅ AFTER: Invalid data rejected at construction
NumberOfEnrollments num = new NumberOfEnrollments(-999);  // throws IllegalArgumentException
```

### 2. **Fail Fast**
```java
// Validation happens at the boundary (construction time)
// Not scattered across service/controller layers
// If it compiles and constructs, it's valid
```

### 3. **Validation Order (Cheap → Expensive)**
```java
1. notNull(value)                    // cheapest
2. inclusiveBetween(min, max, value) // cheap
3. matchesPattern(value, regex)      // expensive (last)
```

### 4. **Immutability**
```java
// Value objects are immutable - operations return new instances
NumberOfEnrollments five = new NumberOfEnrollments(5);
NumberOfEnrollments six = five.increment();  // new instance
// 'five' is unchanged!
```

### 5. **Domain Methods > Setters**
```java
// ❌ Meaningless
enrollment.setValid(false);
enrollment.setInvalidNote("reason");

// ✅ Expressive
enrollment.markAsInvalid(new InvalidNote("Missing Math101 prerequisite"));
```

---

## 📚 Files to Review

1. **[DDD_REFACTORING_NOTES.md](../DDD_REFACTORING_NOTES.md)** - Detailed step-by-step notes
2. **[BEFORE_AFTER_COMPARISON.md](../BEFORE_AFTER_COMPARISON.md)** - Side-by-side comparison
3. **[StudentSubjectEnrollment.java](src/main/java/mk/ukim/finki/wp/commonmodel/enrollments/StudentSubjectEnrollment.java)** - Refactored entity

---

## 🧪 Try It Yourself

### Test Invalid Data (Should Throw):
```java
// All of these throw IllegalArgumentException or NullPointerException:
new InvalidNote("x");                           // too short (min 10 chars)
new InvalidNote("'; DROP TABLE students;--");   // invalid characters
new NumberOfEnrollments(-5);                    // negative
new NumberOfEnrollments(999);                   // too large (max 10)
new GroupName("");                              // empty
new GroupName("A".repeat(51));                  // too long (max 50)
new GroupId("not-a-uuid");                      // invalid format
```

### Test Valid Data (Should Work):
```java
var id = new StudentSubjectEnrollmentId();  // generates UUID
var note = new InvalidNote("Student has not completed Math101 prerequisite");
var num = new NumberOfEnrollments(5);
var groupName = new GroupName("Group A");
var groupId = GroupId.randomGroupId();  // generates UUID

var enrollment = new StudentSubjectEnrollment(semester, student, subject);
enrollment.markAsInvalid(note);
enrollment.assignToGroup(groupName, groupId);
```

---

## 🎓 Learning Resources

- **Book:** "Secure by Design" - Dan Bergh Johnsson, Daniel Deogun, Daniel Sawano
- **Chapter 5:** Domain Primitives
- **Chapter 12:** Validation (Table 12.1 - Refactoring approaches)
- **DDD:** "Domain-Driven Design" - Eric Evans
- **Security:** OWASP Top 10 - Injection attacks

---

## 🚀 Next Steps (Optional)

If you want to extend this project:

1. ✅ **Add Maven/Gradle build** - Create `pom.xml` or `build.gradle`
2. ✅ **Create Repository** - `StudentSubjectEnrollmentRepository extends JpaRepository`
3. ✅ **Create Service** - Business logic layer
4. ✅ **Create REST Controller** - API endpoints
5. ✅ **Write Unit Tests** - Test domain primitives validation
6. ✅ **Write Integration Tests** - Test full flow
7. ✅ **Add application.properties** - Configure Spring Boot + H2
8. ✅ **Run the application** - Test with real HTTP requests

---

## ✨ Summary

**You successfully:**
- ✅ Created DDD base classes
- ✅ Replaced 5 primitive types with validated domain primitives
- ✅ Refactored `StudentSubjectEnrollment` to use domain primitives
- ✅ Removed generic setters, added domain methods
- ✅ Switched from sequential IDs to UUIDs
- ✅ Applied "Secure by Design" principles

**Result:**
- 🛡️ SQL injection prevented (character whitelisting)
- 🛡️ Enumeration attacks prevented (UUIDs)
- 🛡️ Invalid business data prevented (fail-fast validation)
- 🛡️ Type confusion prevented (distinct types)
- 🛡️ Maintainability improved (domain logic in domain)

**Congratulations! You've transformed a primitive-obsessed class into a secure, type-safe domain model.** 🎉
