*/*
Outbox pattern implementation in Java - persisits events to be sent in a database table and processes them asynchronously.
Priority levels for events.
Priority Queue implementation.
Idempotency handling using unique event identifiers.
Pluggable transport mechanisms (e.g., email, SMS, push notifications).
*/
package servicenow;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.sql.*;
import javax.sql.DataSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.*;
enum Channel {
    EMAIL,
    SMS,
    PUSH_NOTIFICATION
}
enum Priority {
    HIGH,
    MEDIUM,
    LOW
}
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "id", "type", "recipient", "content", "priority", "status", "attempts", "lastAttemptedAt", "createdAt" })
class NotificationEvent {
    @JsonProperty("id")
    public String id;
    @JsonProperty("type")
    public Channel type;
    @JsonProperty("recipient")
    public String recipient;
    @JsonProperty("content")
    public String content;
    @JsonProperty("priority")
    public Priority priority;
    @JsonProperty("status")
    public String status; // PENDING, SENT, FAILED
    @JsonProperty("attempts")
    public int attempts;
    @JsonProperty("lastAttemptedAt")
    public Timestamp lastAttemptedAt;
    @JsonProperty("createdAt")
    public Timestamp createdAt;
    @JsonCreator
    public NotificationEvent(@JsonProperty("id") String id,
                             @JsonProperty("type") Channel type,
                             @JsonProperty("recipient") String recipient,
                             @JsonProperty("content") String content,
                             @JsonProperty("priority") Priority priority,
                             @JsonProperty("status") String status,
                             @JsonProperty("attempts") int attempts,
                             @JsonProperty("lastAttemptedAt") Timestamp lastAttemptedAt,
                             @JsonProperty("createdAt") Timestamp createdAt) {
        this.id = id;
        this.type = type;
        this.recipient = recipient;
        this.content = content;
        this.priority = priority;
        this.status = status;
        this.attempts = attempts;
        this.lastAttemptedAt = lastAttemptedAt; 
        this.createdAt = createdAt; 
    }
}
interface Transport {
    boolean send(NotificationEvent event);
}
class EmailTransport implements Transport {
    private static final Logger logger = Logger.getLogger(EmailTransport.class.getName());
    @Override
    public boolean send(NotificationEvent event) {
        // Simulate email sending
        logger.info("Sending Email to " + event.recipient + ": " + event.content);
        return true; // Assume success
    }
}
class SMSTransport implements Transport {
    private static final Logger logger = Logger.getLogger(SMSTransport.class.getName());
    @Override
    public boolean send(NotificationEvent event) {
        // Simulate SMS sending
        logger.info("Sending SMS to " + event.recipient + ": " + event.content);
        return true; // Assume success
    }
}
class PushNotificationTransport implements Transport {
    private static final Logger logger = Logger.getLogger(PushNotificationTransport.class.getName());
    @Override
    public boolean send(NotificationEvent event) {
        // Simulate push notification sending
        logger.info("Sending Push Notification to " + event.recipient + ": " + event.content);
        return true; // Assume success
    }
}
class NotificationService {
    private final DataSource dataSource;
    private final Map<Channel, Transport> transportMap;
    private final ScheduledExecutorService scheduler;
    private final ObjectMapper objectMapper;
    private static final Logger logger = Logger.getLogger(NotificationService.class.getName());
    public NotificationService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.transportMap = new HashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // Register default transports
        transportMap.put(Channel.EMAIL, new EmailTransport());
        transportMap.put(Channel.SMS, new SMSTransport());
        transportMap.put(Channel.PUSH_NOTIFICATION, new PushNotificationTransport());
        // Start the event processing scheduler
        scheduler.scheduleAtFixedRate(this::processEvents, 0, 1, TimeUnit.SECONDS);
    }
    public void registerTransport(Channel channel, Transport transport) {
        transportMap.put(channel, transport);
    }
    public void enqueueEvent(NotificationEvent event) {
        String sql = "INSERT INTO notification_events (id, type, recipient, content, priority, status, attempts, last_attempted_at, created_at) VALUES (?, ?, ?, ?, ?, ?,   ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.id);
            ps.setString(2, event.type.name());
            ps.setString(3, event.recipient);
            ps.setString(4, event.content);
            ps.setString(5, event.priority.name());
            ps.setString(6, "PENDING");
            ps.setInt(7, event.attempts);
            ps.setTimestamp(8, event.lastAttemptedAt);
            ps.setTimestamp(9, event.createdAt);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error enqueuing notification event", e);
        }
    } 

    private void processEvents() {
        String sql = "SELECT * FROM notification_events WHERE status = 'PENDING' ORDER BY priority DESC, created_at ASC LIMIT 10";
        List<NotificationEvent> events = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NotificationEvent event = new NotificationEvent(
                    rs.getString("id"),
                    Channel.valueOf(rs.getString("type")),
                    rs.getString("recipient"),
                    rs.getString("content"),
                    Priority.valueOf(rs.getString("priority")),
                    rs.getString("status"),
                    rs.getInt("attempts"),
                    rs.getTimestamp("last_attempted_at"),
                    rs.getTimestamp("created_at")
                );
                events.add(event);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching notification events", e);
            return;
        }
        for (NotificationEvent event : events) {
            Transport transport = transportMap.get(event.type);
            if (transport != null) {
                boolean success = transport.send(event);
                updateEventStatus(event, success);
            } else {
                logger.warning("No transport registered for channel: " + event.type);
            }
        }
    }
    private void updateEventStatus(NotificationEvent event, boolean success) {
        String sql = "UPDATE notification_events SET status = ?, attempts = ?, last_attempted_at = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, success ? "SENT" : "FAILED");
            ps.setInt(2, event.attempts + 1);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, event.id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating notification event status", e);
        }
    }
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
/*
You are building a monitoring service. A critical component of this service is a log rate limiter that ensures the system does not get overwhelmed by excessive logging. The log rate limiter should allow a maximum of 10 log entries per second. If the limit is exceeded, additional log entries should be discarded until the next second.
Implement a class LogRateLimiter with the following methods:
- void log(String message): Logs a message if the rate limit has not been exceeded. If the limit is exceeded, the message should be discarded.
- int getLogCount(): Returns the number of log entries that have been successfully logged in the current second.
You can use System.currentTimeMillis() to get the current time in milliseconds.
*/
Public class TheLogRateLimiter {
    private final int MAX_LOGS_PER_SECOND = 10;
    private long currentSecond;
    private int logCount;
    private final Object lock = new Object();
    public TheLogRateLimiter() {
        this.currentSecond = System.currentTimeMillis() /1000;
        this.logCount = 0;
    }
    public void log(String message) {
        long now = System.currentTimeMillis() /1000;
        synchronized (lock) {
            if (now != currentSecond) {
                currentSecond = now;
                logCount = 0;   
    }
            if (logCount < MAX_LOGS_PER_SECOND) {
                System.out.println(message);
                logCount++;
            }
        }
    }
    public int getLogCount() {
        long now = System.currentTimeMillis() /1000;
        synchronized (lock) {
            if (now != currentSecond) {
                currentSecond = now;
                logCount = 0;
            }
            return logCount;            
        }
    }   
}   

    