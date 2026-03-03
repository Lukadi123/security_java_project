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
