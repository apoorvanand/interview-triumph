Functional Req:
1. Send Push  moble, devices, in app notifications, emails, ... channels 
NFR -
- high availability, low latency, high throughput, durable 

Estimation - 
500 M DAU - 20 notiications/day = 10 Billion notifications/day
Write QPS - 120 k/sec, storage = 10 TB/day
High - level Design -
1. API gateway - entrypoint 
2. notification service - core logic creating and validating notification
3. Message queue - decoupling notification service with actual dispatcher/workers 
4. workers -smts, sms, push
5. user preference service - maps user preference and like its quiet hours etc
6. database - should have the notification metadata - cassandra write heavy 

Data Model - 
1. Notification = notification_id, user_id, target_device_token, content, status, retry count
2. userpreference = user_id, is_push_enabled, is_email_enabled

Deep dive - 
1. Fan out - a single event might trigger notifications to multiple devices for a user. the system needs to fan this out. A message queue 
2. Retry logic - the push worker should fail the message, which goes to DLQ. A separate process should be able to retry message from this DLQ with exponential backoff
3. Idempotency - the client might trigger the same notification request twice due to network issue. unique notification id to ensure it is only processed once

How 