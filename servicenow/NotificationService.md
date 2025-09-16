```
Notification System (emails, retries, priorities)

Key ideas

Outbox pattern for reliability (persist first, then deliver).

Priority queues + retry with exponential backoff.

Idempotency via requestId.

Pluggable ChannelProviders (Email/SMS/Push).

Core classes & APIs
// Domain
enum Channel { EMAIL, SMS, PUSH }
enum Priority { HIGH, MEDIUM, LOW }
enum Status { PENDING, SENT, FAILED, DEAD_LETTER }

final class NotificationRequest {
    final String requestId;     // idempotency key
    final Channel channel;
    final String destination;   // email/phone/token
    final String body;
    final Priority priority;
    NotificationRequest(String requestId, Channel channel, String destination, String body, Priority priority) {
        this.requestId = requestId; this.channel = channel; this.destination = destination;
        this.body = body; this.priority = priority;
    }
}

// Persistence entity (maps to outbox table)
class Notification {
    Long id;
    String requestId;
    Channel channel;
    String destination;
    String body;
    Priority priority;
    Status status;
    int attempt;              // retry count
    long nextVisibleAtEpochMs;// for delay/backoff scheduling
    long createdAt;
    long updatedAt;
}

// Repository (DB-backed in prod; in-memory for demo)
interface NotificationRepository {
    boolean existsByRequestId(String requestId);
    Notification save(Notification n);
    List<Notification> pullDueBatchOrderedByPriority(int limit, long nowEpochMs);
    void markSent(long id);
    void markFailedAndScheduleRetry(long id, int nextAttempt, long nextVisibleAt);
    void moveToDeadLetter(long id);
}

// Channel providers
interface ChannelProvider {
    Channel channel();
    void send(String destination, String body) throws Exception;
}

class EmailProvider implements ChannelProvider {
    public Channel channel() { return Channel.EMAIL; }
    public void send(String destination, String body) throws Exception {
        // call SMTP/SES/etc.
    }
}

// Retry policy
class RetryPolicy {
    private final int maxAttempts = 5;
    long nextBackoffMs(int attempt) { // 1s, 2s, 4s, 8s, 16s + jitter
        long base = 1000L << Math.min(attempt - 1, 4);
        long jitter = (long) (Math.random() * 250);
        return base + jitter;
    }
    boolean shouldRetry(int attempt) { return attempt < maxAttempts; }
}

// Service + Worker
class NotificationService {
    private final NotificationRepository repo;
    private final Map<Channel, ChannelProvider> providers;
    private final RetryPolicy retry = new RetryPolicy();
    private final ScheduledExecutorService poller = Executors.newSingleThreadScheduledExecutor();
    private final ExecutorService workers = Executors.newFixedThreadPool(8);

    NotificationService(NotificationRepository repo, List<ChannelProvider> provs) {
        this.repo = repo;
        this.providers = new HashMap<>();
        for (ChannelProvider p : provs) providers.put(p.channel(), p);
    }

    // Public API: submit request (idempotent)
    public void submit(NotificationRequest req) {
        if (repo.existsByRequestId(req.requestId)) return; // idempotency
        Notification n = new Notification();
        n.requestId = req.requestId; n.channel = req.channel; n.destination = req.destination;
        n.body = req.body; n.priority = req.priority; n.status = Status.PENDING;
        n.attempt = 0; n.nextVisibleAtEpochMs = System.currentTimeMillis();
        repo.save(n);
    }

    // Start polling & dispatching
    public void start() {
        poller.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            List<Notification> batch = repo.pullDueBatchOrderedByPriority(200, now);
            for (Notification n : batch) {
                workers.submit(() -> deliverOne(n));
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
    }

    private void deliverOne(Notification n) {
        try {
            providers.get(n.channel).send(n.destination, n.body);
            repo.markSent(n.id);
        } catch (Exception ex) {
            int nextAttempt = n.attempt + 1;
            if (retry.shouldRetry(nextAttempt)) {
                long nextTime = System.currentTimeMillis() + retry.nextBackoffMs(nextAttempt);
                repo.markFailedAndScheduleRetry(n.id, nextAttempt, nextTime);
            } else {
                repo.moveToDeadLetter(n.id);
            }
        }
    }
}


DB choice & concurrency

DB: Postgres/MySQL (Outbox table with index on (status, next_visible_at, priority)).

Concurrency: multiple workers safe because pullDueBatch... uses FOR UPDATE SKIP LOCKED to avoid double delivery.

Idempotency: unique index on request_id.

REST endpoints (sketch)

POST /notifications → NotificationRequest

GET /notifications/{requestId} → status tracking

GET /health
```