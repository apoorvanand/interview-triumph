Great topic 👌 — let’s go step by step.

---

# 🔹 What is Idempotency?

**Definition:**
An operation is *idempotent* if **performing it multiple times has the same effect as performing it once**.

* Mathematically: `f(f(x)) = f(x)`
* In distributed systems: calling the same API/transaction multiple times **shouldn’t cause unintended side effects**.

---

# 🔹 Examples

✅ **Idempotent**

* Setting a user’s email to `"abc@example.com"` (doing it once or ten times → state is the same).
* `DELETE /resource/123` → whether called once or twice, resource is gone.
* `PUT /balance/123 { amount: 500 }` → overwrites, not accumulates.

❌ **Non-idempotent**

* `POST /order` → each call creates a *new* order.
* `POST /withdraw?amount=100` → each call withdraws another 100.
* Incrementing a counter.

---

# 🔹 Why is it Important?

* **Network retries:** Clients, load balancers, or service meshes may retry requests on timeout. Without idempotency, retries = duplicates.
* **At-least-once delivery:** Message queues like Kafka/SQS may deliver a message more than once.
* **Fault tolerance:** In distributed transactions, participants may get “commit” messages multiple times.

---

# 🔹 How to Achieve Idempotency

### 1. **Idempotency Keys (request identifiers)**

* Client sends a unique `Idempotency-Key` header.
* Server stores key + result in a database/cache.
* On retry, server returns the *same result* instead of executing again.
  👉 Stripe, PayPal, and many APIs use this.

### 2. **Design APIs carefully**

* Prefer **PUT** (replace) over **POST** (create).
* Use **DELETE** with resource ID (safe to retry).

### 3. **De-duplication logic**

* Database constraints (e.g., unique key on `orderId`).
* Application logic: ignore duplicates if already processed.

### 4. **At-least-once vs Exactly-once**

* True “exactly once” delivery is *hard* in distributed systems.
* Most systems settle for *at-least-once with idempotent operations*.

---

# 🔹 Idempotency in Protocols

* **HTTP**: RFC 7231 defines `GET`, `PUT`, `DELETE`, `HEAD` as idempotent; `POST` usually not.
* **3PC / 2PC**: Commit/Abort messages are made idempotent (participant can safely apply them multiple times).
* **gRPC / REST**: Retry policies assume operations are idempotent, otherwise clients risk side effects.

---

# 🔹 Real-World Examples

* **Payment APIs**: Deducting money must not happen twice if a retry occurs. Use transaction IDs/idempotency keys.
* **Booking systems**: Reserve seat → must not double-book even if retries happen.
* **Database operations**: `UPDATE balance SET amount=500` is idempotent; `UPDATE balance SET amount=amount-100` is not.

---

✅ **In one line:**
Idempotency is the property that makes retries safe in unreliable distributed systems.


Awesome—let’s wire up **payment idempotency** end-to-end the way Stripe/PayPal do it: a **client-supplied Idempotency-Key** + a **server-side de-dup store** guarded by a **unique constraint** and **transactional logic** that guarantees “same request → same effect/response”.

Below is a compact but production-ready Java/Spring Boot blueprint (works with Postgres). It covers REST; I also note gRPC tweaks at the end.

---

# API contract (REST)

* **POST** `/payments`
* Headers:

  * `Idempotency-Key: <uuid-128+ bits>` (required)
  * `Content-Hash: <sha256-of-body>` (optional but recommended to detect mismatched reuse)
* Body (example):

  ```json
  { "amount": 1999, "currency": "INR", "customerId": "cust_42", "paymentMethod": "pm_abc" }
  ```
* Responses:

  * `201 Created` with the payment result on first success
  * **On retry with same key** → return **the exact same body + status** (200/201) and `Idempotent-Replay: true`
  * `409 Conflict` if the same key is currently being processed (or return `202 Accepted` + `Retry-After`)
  * `422 Unprocessable Entity` if the same key is reused with a **different body hash**

---

# DB schema (Postgres)

```sql
CREATE TYPE payment_status AS ENUM ('RECEIVED','PROCESSING','SUCCEEDED','FAILED');

CREATE TABLE payments (
  id                BIGSERIAL PRIMARY KEY,
  request_id        TEXT NOT NULL,               -- the Idempotency-Key
  request_hash      TEXT NOT NULL,               -- sha256 of request body (or canonical subset)
  amount_cents      BIGINT NOT NULL,
  currency          TEXT NOT NULL,
  customer_id       TEXT NOT NULL,
  provider_txn_id   TEXT,                        -- gateway reference
  status            payment_status NOT NULL,
  response_json     JSONB,                       -- full response snapshot to replay
  created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Strong guard: only one row per idempotency key
CREATE UNIQUE INDEX ux_payments_request_id ON payments(request_id);

-- Optional: ensure (request_id, request_hash) pairs remain consistent
-- Or validate hash in app logic and 422 on mismatch.
```

---

# Spring Boot code (concise)

### DTOs

```java
// PaymentDtos.java
public record CreatePaymentRequest(long amount, String currency, String customerId, String paymentMethod) {}
public record PaymentResponse(String paymentId, String status, String providerTxnId) {}
```

### Entity

```java
// Payment.java
import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

@Entity
@Table(name = "payments", indexes = { @Index(name="ux_payments_request_id", columnList="request_id", unique=true) })
public class Payment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="request_id", nullable=false, unique=true) private String requestId;
  @Column(name="request_hash", nullable=false)            private String requestHash;

  @Column(name="amount_cents", nullable=false) private long amountCents;
  @Column(name="currency", nullable=false)     private String currency;
  @Column(name="customer_id", nullable=false)  private String customerId;
  @Column(name="provider_txn_id")              private String providerTxnId;

  @Enumerated(EnumType.STRING)
  @Column(nullable=false) private Status status;

  @Column(name="response_json", columnDefinition="jsonb") private String responseJson;

  @Column(name="created_at", nullable=false) private Instant createdAt = Instant.now();
  @UpdateTimestamp @Column(name="updated_at", nullable=false) private Instant updatedAt;

  public enum Status { RECEIVED, PROCESSING, SUCCEEDED, FAILED }

  // getters/setters/ctors omitted for brevity
}
```

### Repository

```java
// PaymentRepository.java
import org.springframework.data.jpa.repository.*;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
  Optional<Payment> findByRequestId(String requestId);
}
```

### Service (idempotency core)

```java
// PaymentService.java
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PaymentService {
  private final PaymentRepository repo;
  private final PaymentGateway gateway; // your provider adapter
  private final ObjectMapper om = new ObjectMapper();

  public PaymentService(PaymentRepository repo, PaymentGateway gateway) {
    this.repo = repo; this.gateway = gateway;
  }

  @Transactional
  public PaymentResponse create(String idempotencyKey, String reqHash, CreatePaymentRequest req) {
    // Fast-path: already exists?
    var existing = repo.findByRequestId(idempotencyKey).orElse(null);
    if (existing != null) {
      if (!existing.getRequestHash().equals(reqHash)) {
        throw new IdempotencyMismatchException("Same Idempotency-Key with different body");
      }
      // If terminal, replay exact response
      if (existing.getStatus() == Payment.Status.SUCCEEDED || existing.getStatus() == Payment.Status.FAILED) {
        return toResponse(existing, /*replay=*/true);
      }
      // In-flight → conflict (or return 202)
      throw new InFlightException("Processing");
    }

    // Try to claim the key by inserting a RECEIVED row
    Payment p = new Payment();
    p.setRequestId(idempotencyKey);
    p.setRequestHash(reqHash);
    p.setAmountCents(req.amount());
    p.setCurrency(req.currency());
    p.setCustomerId(req.customerId());
    p.setStatus(Payment.Status.RECEIVED);

    try {
      p = repo.saveAndFlush(p);
    } catch (DataIntegrityViolationException dup) {
      // Lost the race; someone else inserted first → fetch and handle as above
      var raced = repo.findByRequestId(idempotencyKey).orElseThrow();
      if (!raced.getRequestHash().equals(reqHash)) throw new IdempotencyMismatchException("Different body");
      if (raced.getStatus() == Payment.Status.SUCCEEDED || raced.getStatus() == Payment.Status.FAILED)
        return toResponse(raced, true);
      throw new InFlightException("Processing");
    }

    // Mark PROCESSING (optional state; useful for observability)
    p.setStatus(Payment.Status.PROCESSING);
    repo.save(p);

    // Call provider outside of external transaction side-effects? Keep the DB tx open for row only.
    var result = gateway.charge(idempotencyKey, req); // pass key to provider if supported
    // Persist terminal state & full response for replay
    p.setProviderTxnId(result.providerTxnId());
    p.setStatus(result.success() ? Payment.Status.SUCCEEDED : Payment.Status.FAILED);
    p.setResponseJson(writeJson(new PaymentResponse(String.valueOf(p.getId()),
                                                    p.getStatus().name(),
                                                    p.getProviderTxnId())));
    repo.save(p);

    return new PaymentResponse(String.valueOf(p.getId()), p.getStatus().name(), p.getProviderTxnId());
  }

  private PaymentResponse toResponse(Payment p, boolean replay) {
    try {
      var resp = om.readValue(p.getResponseJson(), PaymentResponse.class);
      return resp;
    } catch (Exception e) {
      return new PaymentResponse(String.valueOf(p.getId()), p.getStatus().name(), p.getProviderTxnId());
    }
  }

  private String writeJson(Object o) {
    try { return om.writeValueAsString(o); } catch (Exception e) { throw new RuntimeException(e); }
  }
}
```

### Controller

```java
// PaymentController.java
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {
  private final PaymentService svc;

  public PaymentController(PaymentService svc) { this.svc = svc; }

  @PostMapping
  public ResponseEntity<PaymentResponse> create(
      @RequestHeader("Idempotency-Key") String idemKey,
      @RequestHeader(value="Content-Hash", required=false) String contentHash,
      @RequestBody CreatePaymentRequest body) {

    var hash = contentHash != null ? contentHash
              : DigestUtils.sha256Hex((body.amount()+":"+body.currency()+":"+body.customerId()+":"+body.paymentMethod()));

    try {
      var resp = svc.create(idemKey, hash, body);
      // 201 for first-time success; if this was a replay, still fine to return 200/201 consistently
      return ResponseEntity.status(HttpStatus.CREATED)
                           .header("Idempotent-Replay", "false")
                           .body(resp);
    } catch (IdempotencyMismatchException e) {
      return ResponseEntity.unprocessableEntity().build(); // 422
    } catch (InFlightException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
                           .header(HttpHeaders.RETRY_AFTER, "2") // seconds
                           .build();
    }
  }
}
```

### Provider adapter (sketch)

```java
// PaymentGateway.java
public interface PaymentGateway {
  record Result(boolean success, String providerTxnId) {}
  Result charge(String idempotencyKey, CreatePaymentRequest req);
}
```

### Small custom exceptions

```java
class IdempotencyMismatchException extends RuntimeException {
  IdempotencyMismatchException(String m){ super(m); }
}
class InFlightException extends RuntimeException {
  InFlightException(String m){ super(m); }
}
```

---

## Key behaviors you now have

1. **Exactly-once *effect*** (not delivery):

   * Unique `(request_id)` ensures only one row can be created per key.
   * Retries with the same key return the **same response** from `response_json`.

2. **Body mismatch protection:**

   * Same key + different payload → **422** (prevents accidental reuse).

3. **Race-safety:**

   * Two concurrent calls with same key: one inserts; the other sees the existing row and **does not** call the gateway again.

4. **Recovery/replay:**

   * If the client times out and retries, server replays the stored response—no duplicate charge.

---

## Optional hardening

* **Redis short-TTL mirror** for hot replays: cache `Idempotency-Key → response_json` (DB remains source of truth).
* **Outbox pattern** for side effects (emails, webhooks) so replays don’t resend.
* **Provider support:** pass the same idempotency key to gateway (Stripe/Adyen support this).
* **Locks:** if you prefer, use `SELECT … FOR UPDATE` on the claimed row; current unique-insert approach is usually enough.
* **TTL policy:** you may prune old idempotency records (e.g., >90 days), but keep them long enough to match business dispute windows.

---

## gRPC variant (quick notes)

* Put the key in **gRPC metadata** (e.g., `idempotency-key`).
* Server interceptor validates presence, computes request hash, and routes through the same service logic above.
* Return a `replay=true` response field or response trailer if you need to signal a replay.

---
Perfect — here’s a **Spring Boot** implementation of payment idempotency with **unit tests (JUnit + Mockito)** and **integration tests (SpringBootTest + Testcontainers + MockMvc)**. It uses a client-supplied `Idempotency-Key` and a DB-enforced unique constraint to guarantee “same request → same effect/response”.

---

# Project setup

## build.gradle.kts

```kotlin
plugins {
    id("java")
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.5"
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }

repositories { mavenCentral() }

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("org.apache.commons:commons-codec:1.17.1")

    runtimeOnly("org.postgresql:postgresql:42.7.4")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter:1.20.2")
    testImplementation("org.testcontainers:postgresql:1.20.2")
    testImplementation("org.mockito:mockito-core:5.13.0")
}
tasks.test { useJUnitPlatform() }
```

## application.yml (main)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/payments
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  liquibase:
    enabled: true
```

## db/changelog/db.changelog-master.yaml (Liquibase)

```yaml
databaseChangeLog:
  - changeSet:
      id: 001-create-payments
      author: you
      changes:
        - sql: |
            CREATE TYPE payment_status AS ENUM ('RECEIVED','PROCESSING','SUCCEEDED','FAILED');
            CREATE TABLE IF NOT EXISTS payments (
              id                BIGSERIAL PRIMARY KEY,
              request_id        TEXT NOT NULL UNIQUE,
              request_hash      TEXT NOT NULL,
              amount_cents      BIGINT NOT NULL,
              currency          TEXT NOT NULL,
              customer_id       TEXT NOT NULL,
              provider_txn_id   TEXT,
              status            payment_status NOT NULL,
              response_json     JSONB,
              created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
              updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
            );
```

---

# Application code

## DTOs

```java
// src/main/java/com/example/payments/api/PaymentDtos.java
package com.example.payments.api;

public record CreatePaymentRequest(long amount, String currency, String customerId, String paymentMethod) {}
public record PaymentResponse(String paymentId, String status, String providerTxnId) {}
```

## Entity

```java
// src/main/java/com/example/payments/model/Payment.java
package com.example.payments.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

@Entity
@Table(name = "payments")
public class Payment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="request_id", nullable=false, unique=true) private String requestId;
  @Column(name="request_hash", nullable=false)            private String requestHash;

  @Column(name="amount_cents", nullable=false) private long amountCents;
  @Column(name="currency",     nullable=false) private String currency;
  @Column(name="customer_id",  nullable=false) private String customerId;
  @Column(name="provider_txn_id")              private String providerTxnId;

  public enum Status { RECEIVED, PROCESSING, SUCCEEDED, FAILED }
  @Enumerated(EnumType.STRING) @Column(nullable=false) private Status status;

  @Column(name="response_json", columnDefinition="jsonb") private String responseJson;

  @Column(name="created_at", nullable=false) private Instant createdAt = Instant.now();
  @UpdateTimestamp @Column(name="updated_at", nullable=false) private Instant updatedAt;

  // getters/setters
  public Long getId() { return id; }
  public String getRequestId() { return requestId; }
  public void setRequestId(String requestId) { this.requestId = requestId; }
  public String getRequestHash() { return requestHash; }
  public void setRequestHash(String requestHash) { this.requestHash = requestHash; }
  public long getAmountCents() { return amountCents; }
  public void setAmountCents(long amountCents) { this.amountCents = amountCents; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
  public String getCustomerId() { return customerId; }
  public void setCustomerId(String customerId) { this.customerId = customerId; }
  public String getProviderTxnId() { return providerTxnId; }
  public void setProviderTxnId(String providerTxnId) { this.providerTxnId = providerTxnId; }
  public Status getStatus() { return status; }
  public void setStatus(Status status) { this.status = status; }
  public String getResponseJson() { return responseJson; }
  public void setResponseJson(String responseJson) { this.responseJson = responseJson; }
}
```

## Repository

```java
// src/main/java/com/example/payments/repo/PaymentRepository.java
package com.example.payments.repo;

import com.example.payments.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
  Optional<Payment> findByRequestId(String requestId);
}
```

## Gateway adapter (fake for demo)

```java
// src/main/java/com/example/payments/gateway/PaymentGateway.java
package com.example.payments.gateway;

import com.example.payments.api.CreatePaymentRequest;

public interface PaymentGateway {
  record Result(boolean success, String providerTxnId) {}
  Result charge(String idempotencyKey, CreatePaymentRequest req);
}
```

```java
// src/main/java/com/example/payments/gateway/FakeGateway.java
package com.example.payments.gateway;

import com.example.payments.api.CreatePaymentRequest;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class FakeGateway implements PaymentGateway {
  @Override
  public Result charge(String idempotencyKey, CreatePaymentRequest req) {
    // pretend success and return a deterministic-ish id (could also echo idempotencyKey)
    return new Result(true, "gw_" + UUID.nameUUIDFromBytes(idempotencyKey.getBytes()));
  }
}
```

## Exceptions

```java
// src/main/java/com/example/payments/service/IdempotencyMismatchException.java
package com.example.payments.service;
public class IdempotencyMismatchException extends RuntimeException {
  public IdempotencyMismatchException(String m) { super(m); }
}

// src/main/java/com/example/payments/service/InFlightException.java
package com.example.payments.service;
public class InFlightException extends RuntimeException {
  public InFlightException(String m) { super(m); }
}
```

## Service (core idempotency)

```java
// src/main/java/com/example/payments/service/PaymentService.java
package com.example.payments.service;

import com.example.payments.api.CreatePaymentRequest;
import com.example.payments.api.PaymentResponse;
import com.example.payments.gateway.PaymentGateway;
import com.example.payments.model.Payment;
import com.example.payments.repo.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
  private final PaymentRepository repo;
  private final PaymentGateway gateway;
  private final ObjectMapper om = new ObjectMapper();

  public PaymentService(PaymentRepository repo, PaymentGateway gateway) {
    this.repo = repo; this.gateway = gateway;
  }

  @Transactional
  public PaymentResponse create(String idemKey, String reqHash, CreatePaymentRequest req) {
    var existing = repo.findByRequestId(idemKey).orElse(null);
    if (existing != null) {
      if (!existing.getRequestHash().equals(reqHash)) throw new IdempotencyMismatchException("different body");
      if (existing.getStatus() == Payment.Status.SUCCEEDED || existing.getStatus() == Payment.Status.FAILED) {
        return parse(existing);
      }
      throw new InFlightException("processing");
    }

    var p = new Payment();
    p.setRequestId(idemKey);
    p.setRequestHash(reqHash);
    p.setAmountCents(req.amount());
    p.setCurrency(req.currency());
    p.setCustomerId(req.customerId());
    p.setStatus(Payment.Status.RECEIVED);

    try {
      p = repo.saveAndFlush(p);
    } catch (DataIntegrityViolationException dup) {
      var raced = repo.findByRequestId(idemKey).orElseThrow();
      if (!raced.getRequestHash().equals(reqHash)) throw new IdempotencyMismatchException("different body");
      if (raced.getStatus() == Payment.Status.SUCCEEDED || raced.getStatus() == Payment.Status.FAILED) return parse(raced);
      throw new InFlightException("processing");
    }

    p.setStatus(Payment.Status.PROCESSING);
    repo.save(p);

    var res = gateway.charge(idemKey, req);
    p.setProviderTxnId(res.providerTxnId());
    p.setStatus(res.success() ? Payment.Status.SUCCEEDED : Payment.Status.FAILED);

    var body = new PaymentResponse(String.valueOf(p.getId()), p.getStatus().name(), p.getProviderTxnId());
    try { p.setResponseJson(om.writeValueAsString(body)); } catch (Exception e) { /* ignore */ }
    repo.save(p);

    return body;
  }

  private PaymentResponse parse(Payment p) {
    try { return om.readValue(p.getResponseJson(), PaymentResponse.class); }
    catch (Exception e) { return new PaymentResponse(String.valueOf(p.getId()), p.getStatus().name(), p.getProviderTxnId()); }
  }
}
```

## Controller

```java
// src/main/java/com/example/payments/api/PaymentController.java
package com.example.payments.api;

import com.example.payments.service.IdempotencyMismatchException;
import com.example.payments.service.InFlightException;
import com.example.payments.service.PaymentService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {
  private final PaymentService svc;
  public PaymentController(PaymentService svc) { this.svc = svc; }

  @PostMapping
  public ResponseEntity<PaymentResponse> create(
      @RequestHeader("Idempotency-Key") String idemKey,
      @RequestHeader(value="Content-Hash", required=false) String contentHash,
      @RequestBody CreatePaymentRequest body) {

    var hash = contentHash != null ? contentHash
      : DigestUtils.sha256Hex(body.amount()+":"+body.currency()+":"+body.customerId()+":"+body.paymentMethod());

    try {
      var resp = svc.create(idemKey, hash, body);
      return ResponseEntity.status(HttpStatus.CREATED)
          .header("Idempotent-Replay", "false")
          .body(resp);
    } catch (IdempotencyMismatchException e) {
      return ResponseEntity.unprocessableEntity().build();
    } catch (InFlightException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .header(HttpHeaders.RETRY_AFTER, "2")
          .build();
    }
  }
}
```

---

# Unit tests (service-level, Mockito)

```java
// src/test/java/com/example/payments/service/PaymentServiceTest.java
package com.example.payments.service;

import com.example.payments.api.CreatePaymentRequest;
import com.example.payments.api.PaymentResponse;
import com.example.payments.gateway.PaymentGateway;
import com.example.payments.model.Payment;
import com.example.payments.repo.PaymentRepository;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

  PaymentRepository repo = mock(PaymentRepository.class);
  PaymentGateway gateway = mock(PaymentGateway.class);
  PaymentService svc = new PaymentService(repo, gateway);

  @Test
  void first_call_inserts_and_calls_gateway() {
    var req = new CreatePaymentRequest(1999, "INR", "cust1", "pm1");
    when(repo.findByRequestId("k1")).thenReturn(Optional.empty());
    when(repo.saveAndFlush(any())).thenAnswer(inv -> {
      Payment p = inv.getArgument(0);
      // simulate assigned id
      var spy = spy(p);
      doReturn(1L).when(spy).getId();
      return spy;
    });
    when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(gateway.charge(eq("k1"), eq(req))).thenReturn(new PaymentGateway.Result(true, "gw_x"));

    var resp = svc.create("k1","h1",req);

    assertEquals("SUCCEEDED", resp.status());
    assertEquals("gw_x", resp.providerTxnId());
    verify(gateway, times(1)).charge(eq("k1"), eq(req));
  }

  @Test
  void replay_returns_same_response_without_gateway_call() {
    var req = new CreatePaymentRequest(1999, "INR", "cust1", "pm1");
    var p = new Payment();
    p.setRequestId("k1"); p.setRequestHash("h1");
    p.setStatus(Payment.Status.SUCCEEDED);
    p.setProviderTxnId("gw_x");
    p.setResponseJson("{\"paymentId\":\"1\",\"status\":\"SUCCEEDED\",\"providerTxnId\":\"gw_x\"}");

    when(repo.findByRequestId("k1")).thenReturn(Optional.of(p));

    var resp = svc.create("k1","h1",req);
    assertEquals("SUCCEEDED", resp.status());
    assertEquals("gw_x", resp.providerTxnId());
    verifyNoInteractions(gateway);
  }

  @Test
  void mismatch_hash_throws_422_semantics() {
    var req = new CreatePaymentRequest(1999, "INR", "cust1", "pm1");
    var p = new Payment();
    p.setRequestId("k1"); p.setRequestHash("h_different");
    p.setStatus(Payment.Status.SUCCEEDED);
    when(repo.findByRequestId("k1")).thenReturn(Optional.of(p));

    assertThrows(IdempotencyMismatchException.class, () -> svc.create("k1","h1",req));
  }

  @Test
  void concurrent_insert_loses_race_and_replays_or_conflicts() {
    var req = new CreatePaymentRequest(1999, "INR", "cust1", "pm1");
    when(repo.findByRequestId("k1")).thenReturn(Optional.empty());
    when(repo.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("dup"));
    var p = new Payment();
    p.setRequestId("k1"); p.setRequestHash("h1");
    p.setStatus(Payment.Status.SUCCEEDED);
    p.setProviderTxnId("gw_y");
    p.setResponseJson("{\"paymentId\":\"2\",\"status\":\"SUCCEEDED\",\"providerTxnId\":\"gw_y\"}");
    when(repo.findByRequestId("k1")).thenReturn(Optional.of(p)); // after duplicate

    var resp = svc.create("k1","h1",req);
    assertEquals("gw_y", resp.providerTxnId());
    verifyNoInteractions(gateway);
  }
}
```

---

# Integration tests (Testcontainers + MockMvc)

* Spins up **real Postgres** in Docker.
* Boots the full Spring context.
* Verifies:

  1. First call 201 + body,
  2. Second call (same key/body) returns **201** with **identical body** (replay semantics),
  3. Same key + different body → **422**,
  4. Optional: inflight **409** can be tested with a latch (kept simple here).

```java
// src/test/java/com/example/payments/PaymentIntegrationTest.java
package com.example.payments;

import com.example.payments.api.CreatePaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class PaymentIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
      .withDatabaseName("testdb")
      .withUsername("test")
      .withPassword("test");

  @DynamicPropertySource
  static void datasourceProps(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", postgres::getJdbcUrl);
    r.add("spring.datasource.username", postgres::getUsername);
    r.add("spring.datasource.password", postgres::getPassword);
    r.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    r.add("spring.liquibase.enabled", () -> true);
  }

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper om;

  @Test
  void idempotent_flow_happy_path_and_replay_and_mismatch() throws Exception {
    var body = new CreatePaymentRequest(1999, "INR", "cust1", "pm1");
    var json = om.writeValueAsString(body);

    // 1) first call
    var first = mockMvc.perform(post("/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Idempotency-Key", "k-123")
            .content(json))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("SUCCEEDED"))
        .andExpect(jsonPath("$.providerTxnId", startsWith("gw_")))
        .andReturn();

    // Extract response to compare
    var bodyFirst = first.getResponse().getContentAsString();

    // 2) replay with same key + same body => same response
    mockMvc.perform(post("/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Idempotency-Key", "k-123")
            .content(json))
        .andExpect(status().isCreated())
        .andExpect(content().json(bodyFirst)); // exact same

    // 3) same key, different body => 422
    var different = new CreatePaymentRequest(2999, "INR", "cust1", "pm1");
    mockMvc.perform(post("/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Idempotency-Key", "k-123")
            .content(om.writeValueAsString(different)))
        .andExpect(status().isUnprocessableEntity());
  }
}
```

> Notes
> • Liquibase runs against Testcontainers Postgres to create the `payments` table + enum.
> • We assert **exact JSON equality** for the replay.
> • The gateway is the in-app `FakeGateway`, simulating a provider.

---

# What you get

* **Exactly-once effect** for payment creation with safe retries.
* **Race safety** via unique key + transactional insert.
* **Replay** of the original response for identical retried requests.
* **Mismatch protection** (same key, different body → 422).
* **Unit tests** verifying service logic and **integration tests** confirming full HTTP→DB behavior.

---

* add a **Redis** cache layer for hot idempotent replays,
* include a **gRPC** version (with a server interceptor reading `idempotency-key` metadata),
* or extend tests to simulate **in-flight 409** with concurrency (CountDownLatch).

Great — I’ll extend your Spring setup with:

1. **Redis hot-cache** for idempotent replays (+ TTL)
2. **gRPC variant** with an **Idempotency-Key interceptor**
3. **Concurrency test** proving one winner + one `409` on simultaneous calls
4. **Integration tests** using **Testcontainers (Postgres + Redis)**

---

# 1) Add Redis hot-cache (Spring Data Redis)

## `build.gradle.kts` – extra deps

```kotlin
dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-redis")
  testImplementation("org.testcontainers:redis:1.20.2")
  // (keep previous deps)
}
```

## `application.yml` (test/prod override as needed)

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 2s
```

## A tiny Redis config

```java
// src/main/java/com/example/payments/config/RedisConfig.java
package com.example.payments.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {
  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(); // host/port via properties
  }
  @Bean
  public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory cf) {
    return new StringRedisTemplate(cf);
  }
}
```

## Update the `PaymentService` to use Redis (hot replay path)

```java
// src/main/java/com/example/payments/service/PaymentService.java
package com.example.payments.service;

import com.example.payments.api.CreatePaymentRequest;
import com.example.payments.api.PaymentResponse;
import com.example.payments.gateway.PaymentGateway;
import com.example.payments.model.Payment;
import com.example.payments.repo.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
public class PaymentService {
  private static final Duration IDEM_TTL = Duration.ofDays(3);
  private final PaymentRepository repo;
  private final PaymentGateway gateway;
  private final ObjectMapper om = new ObjectMapper();
  private final StringRedisTemplate redis;

  public PaymentService(PaymentRepository repo, PaymentGateway gateway, StringRedisTemplate redis) {
    this.repo = repo; this.gateway = gateway; this.redis = redis;
  }

  private String cacheKey(String idemKey) { return "idem:payments:" + idemKey; }

  @Transactional
  public PaymentResponse create(String idemKey, String reqHash, CreatePaymentRequest req) {
    // 1) Hot cache fast-path
    var cached = redis.opsForValue().get(cacheKey(idemKey));
    if (cached != null) {
      try {
        var cachedResp = om.readValue(cached, PaymentResponse.class);
        return cachedResp; // same response as original (replay)
      } catch (Exception ignore) { /* fall through */ }
    }

    // 2) DB lookup for correctness (and hash check)
    var existing = repo.findByRequestId(idemKey).orElse(null);
    if (existing != null) {
      if (!existing.getRequestHash().equals(reqHash)) throw new IdempotencyMismatchException("different body");
      if (existing.getStatus() == Payment.Status.SUCCEEDED || existing.getStatus() == Payment.Status.FAILED) {
        var resp = parse(existing);
        // backfill cache
        try { redis.opsForValue().set(cacheKey(idemKey), om.writeValueAsString(resp), IDEM_TTL); } catch (Exception ignore) {}
        return resp;
      }
      throw new InFlightException("processing");
    }

    // 3) Claim key via unique insert
    var p = new Payment();
    p.setRequestId(idemKey);
    p.setRequestHash(reqHash);
    p.setAmountCents(req.amount());
    p.setCurrency(req.currency());
    p.setCustomerId(req.customerId());
    p.setStatus(Payment.Status.RECEIVED);

    try {
      p = repo.saveAndFlush(p);
    } catch (DataIntegrityViolationException dup) {
      var raced = repo.findByRequestId(idemKey).orElseThrow();
      if (!raced.getRequestHash().equals(reqHash)) throw new IdempotencyMismatchException("different body");
      if (raced.getStatus() == Payment.Status.SUCCEEDED || raced.getStatus() == Payment.Status.FAILED) {
        var resp = parse(raced);
        try { redis.opsForValue().set(cacheKey(idemKey), om.writeValueAsString(resp), IDEM_TTL); } catch (Exception ignore) {}
        return resp;
      }
      throw new InFlightException("processing");
    }

    // 4) Process
    p.setStatus(Payment.Status.PROCESSING);
    repo.save(p);

    var res = gateway.charge(idemKey, req);
    p.setProviderTxnId(res.providerTxnId());
    p.setStatus(res.success() ? Payment.Status.SUCCEEDED : Payment.Status.FAILED);

    var body = new PaymentResponse(String.valueOf(p.getId()), p.getStatus().name(), p.getProviderTxnId());
    try { p.setResponseJson(om.writeValueAsString(body)); } catch (Exception ignore) {}
    repo.save(p);

    // 5) Store replay in cache (with TTL)
    try { redis.opsForValue().set(cacheKey(idemKey), om.writeValueAsString(body), IDEM_TTL); } catch (Exception ignore) {}

    return body;
  }

  private PaymentResponse parse(Payment p) {
    try { return om.readValue(p.getResponseJson(), PaymentResponse.class); }
    catch (Exception e) { return new PaymentResponse(String.valueOf(p.getId()), p.getStatus().name(), p.getProviderTxnId()); }
  }
}
```

---

# 2) gRPC variant with Idempotency-Key interceptor

## Add gRPC deps

```kotlin
dependencies {
  implementation("net.devh:grpc-server-spring-boot-starter:3.0.0.RELEASE")
  // (keep previous deps)
}
```

## Proto (example)

```proto
// src/main/proto/payments.proto
syntax = "proto3";
package payments;

service PaymentService {
  rpc CreatePayment(CreatePaymentRequest) returns (PaymentResponse);
}

message CreatePaymentRequest {
  int64 amount = 1;
  string currency = 2;
  string customerId = 3;
  string paymentMethod = 4;
}

message PaymentResponse {
  string paymentId = 1;
  string status = 2;
  string providerTxnId = 3;
}
```

## Interceptor: extract `idempotency-key` from metadata

```java
// src/main/java/com/example/payments/grpc/IdempotencyInterceptor.java
package com.example.payments.grpc;

import io.grpc.*;
import org.apache.commons.codec.digest.DigestUtils;

public class IdempotencyInterceptor implements ServerInterceptor {
  public static final Context.Key<String> IDEM_KEY_CTX = Context.key("idem-key");
  public static final Context.Key<String> REQ_HASH_CTX = Context.key("req-hash");
  public static final Metadata.Key<String> IDEM_HEADER =
      Metadata.Key.of("idempotency-key", Metadata.ASCII_STRING_MARSHALLER);

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

    String idemKey = headers.get(IDEM_HEADER);
    if (idemKey == null || idemKey.isBlank()) {
      call.close(Status.INVALID_ARGUMENT.withDescription("Missing idempotency-key"), new Metadata());
      return new ServerCall.Listener<>() {};
    }

    // We can’t hash the body here (no access yet). Service will compute hash from request.
    Context ctx = Context.current().withValue(IDEM_KEY_CTX, idemKey);
    return Contexts.interceptCall(ctx, call, headers, next);
  }

  // helper for service to compute a deterministic hash
  public static String hashFor(long amount, String currency, String customerId, String paymentMethod) {
    return DigestUtils.sha256Hex(amount + ":" + currency + ":" + customerId + ":" + paymentMethod);
  }
}
```

## gRPC service implementation (reuses `PaymentService`)

```java
// src/main/java/com/example/payments/grpc/PaymentGrpcService.java
package com.example.payments.grpc;

import com.example.payments.api.CreatePaymentRequest;
import com.example.payments.api.PaymentResponse;
import com.example.payments.service.PaymentService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import payments.PaymentServiceGrpc;
import payments.Payments;

@GrpcService(interceptors = {IdempotencyInterceptor.class})
public class PaymentGrpcService extends PaymentServiceGrpc.PaymentServiceImplBase {

  private final PaymentService paymentService;

  public PaymentGrpcService(PaymentService paymentService) { this.paymentService = paymentService; }

  @Override
  public void createPayment(Payments.CreatePaymentRequest req, StreamObserver<Payments.PaymentResponse> respObs) {
    var idemKey = IdempotencyInterceptor.IDEM_KEY_CTX.get();
    var reqHash = IdempotencyInterceptor.hashFor(req.getAmount(), req.getCurrency(), req.getCustomerId(), req.getPaymentMethod());
    var dtoReq = new CreatePaymentRequest(req.getAmount(), req.getCurrency(), req.getCustomerId(), req.getPaymentMethod());
    PaymentResponse r = paymentService.create(idemKey, reqHash, dtoReq);
    var out = Payments.PaymentResponse.newBuilder()
        .setPaymentId(r.paymentId())
        .setStatus(r.status())
        .setProviderTxnId(r.providerTxnId() == null ? "" : r.providerTxnId())
        .build();
    respObs.onNext(out);
    respObs.onCompleted();
  }
}
```

> Client must send metadata header `idempotency-key: <uuid>` with the RPC.

---

# 3) Concurrency test (prove one winner + one 409)

```java
// src/test/java/com/example/payments/ConcurrentIdempotencyTest.java
package com.example.payments;

import com.example.payments.api.CreatePaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class ConcurrentIdempotencyTest {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
      .withDatabaseName("testdb")
      .withUsername("test")
      .withPassword("test");

  @Container
  static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

  @DynamicPropertySource
  static void props(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", postgres::getJdbcUrl);
    r.add("spring.datasource.username", postgres::getUsername);
    r.add("spring.datasource.password", postgres::getPassword);
    r.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    r.add("spring.liquibase.enabled", () -> true);
    r.add("spring.data.redis.host", () -> redis.getHost());
    r.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
  }

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper om;

  @Test
  void two_simultaneous_requests_same_key_one_wins_other_409() throws Exception {
    var body = new CreatePaymentRequest(1999, "INR", "cust1", "pm1");
    var json = om.writeValueAsString(body);

    var barrier = new CyclicBarrier(2);
    Callable<Integer> task = () -> {
      barrier.await(3, TimeUnit.SECONDS);
      var res = mockMvc.perform(post("/payments")
              .contentType(MediaType.APPLICATION_JSON)
              .header("Idempotency-Key", "k-concurrent")
              .content(json))
          .andReturn()
          .getResponse();
      return res.getStatus();
    };

    var ex = Executors.newFixedThreadPool(2);
    var f1 = ex.submit(task);
    var f2 = ex.submit(task);
    int s1 = f1.get(5, TimeUnit.SECONDS);
    int s2 = f2.get(5, TimeUnit.SECONDS);
    ex.shutdown();

    // Expect exactly one CREATED and one CONFLICT
    Assertions.assertTrue((s1 == 201 && s2 == 409) || (s1 == 409 && s2 == 201),
        "statuses were: " + s1 + ", " + s2);
  }
}
```

---

# 4) Integration test with Redis hot replay

This reuses your earlier integration test but now runs with Redis; the second call should still return the **same JSON** (potentially served from cache).

```java
// src/test/java/com/example/payments/PaymentWithRedisIntegrationTest.java
package com.example.payments;

import com.example.payments.api.CreatePaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class PaymentWithRedisIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
      .withDatabaseName("testdb")
      .withUsername("test")
      .withPassword("test");

  @Container
  static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

  @DynamicPropertySource
  static void props(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", postgres::getJdbcUrl);
    r.add("spring.datasource.username", postgres::getUsername);
    r.add("spring.datasource.password", postgres::getPassword);
    r.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    r.add("spring.liquibase.enabled", () -> true);
    r.add("spring.data.redis.host", () -> redis.getHost());
    r.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
  }

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper om;

  @Test
  void replay_is_identical_and_cache_backed() throws Exception {
    var body = new CreatePaymentRequest(1999, "INR", "cust1", "pm1");
    var json = om.writeValueAsString(body);

    var first = mockMvc.perform(post("/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Idempotency-Key", "k-redis-1")
            .content(json))
        .andExpect(status().isCreated())
        .andReturn();
    var firstJson = first.getResponse().getContentAsString();

    // second call — should be identical (very likely served from Redis)
    mockMvc.perform(post("/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Idempotency-Key", "k-redis-1")
            .content(json))
        .andExpect(status().isCreated())
        .andExpect(content().json(firstJson));
  }
}
```

---

## Notes & production tips

* **TTL**: set based on your business dispute window (e.g., 7–30 days). DB remains source-of-truth; Redis is an optimization.
* **Provider idempotency**: pass the same key to the payment gateway if supported (double safety).
* **Outbox**: if you emit webhooks/emails, use the Outbox pattern so replays don’t duplicate side effects.
* **gRPC clients**: add a client interceptor that always injects the `idempotency-key` metadata (UUID per logical request).

