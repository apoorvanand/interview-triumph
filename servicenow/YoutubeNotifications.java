/*
YouTube Notifications (Observer Pattern) — Java, working code

What you’ll show:

Subject = Channel (YouTube channel)

Observer = Subscriber

Register / unregister observers, notify on new video

Push message (video meta) + simple pull (getLatestVideo)

Thread-safe registration & notification
Explanation
This code implements the Observer Pattern for YouTube notifications in Java.

Subject: Channel (specifically, YouTubeChannel)
Observers: Subscriber (e.g., MobileSubscriber, EmailSubscriber)
Value Object: Video
Thread Safety: Uses CopyOnWriteArrayList for subscribers and synchronizes uploads.
Flow:

Subscribers register/unregister to a channel.
When a new video is uploaded, all subscribers are notified (push).
Subscribers can also pull the latest video.
Diagram
Below is a simple UML-style diagram showing relationships:
+-------------------+         subscribes/unsubscribes         +-------------------+
|   YouTubeChannel  |<--------------------------------------->|    Subscriber     |
|-------------------|                                         |-------------------|
| - subscribers     |                                         | + update(video)   |
| - feed            |                                         | + id()            |
|-------------------|                                         +-------------------+
| + subscribe(s)    |                                                 ^
| + unsubscribe(s)  |                                                 |
| + upload(video)   |                                                 |
| + getLatestVideo()|                                                 |
+-------------------+                                                 |
        |                                                            / \
        | notifies (push)                                            | |
        v                                                            | |
+-------------------+                                        +-------------------+
|      Video        |                                        | MobileSubscriber  |
|-------------------|                                        | EmailSubscriber   |
| + id              |                                        +-------------------+
| + title           |
| + url             |
| + publishedAt     |
+-------------------+

*/
package servicenow;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

// ----- Observer -----
interface Subscriber {
    void update(Video video);         // push model
    String id();
}

// ----- Subject -----
interface Channel {
    void subscribe(Subscriber s);
    void unsubscribe(Subscriber s);
    void upload(Video video);
    Video getLatestVideo();           // pull model (optional)
}

// ----- Value Object -----
final class Video {
    private final String id;
    private final String title;
    private final String url;
    private final long   publishedAt;

    public Video(String id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.publishedAt = System.currentTimeMillis();
    }
    public String id() { return id; }
    public String title() { return title; }
    public String url() { return url; }
    public long publishedAt() { return publishedAt; }

    @Override public String toString() {
        return "Video{id='%s', title='%s', url='%s'}".formatted(id, title, url);
    }
}

// ----- Concrete Subject -----
class YouTubeChannel implements Channel {
    private final String name;
    // CopyOnWriteArrayList = safe iteration during notifications
    private final List<Subscriber> subscribers = new CopyOnWriteArrayList<>();
    // Keep last N videos, for optional pull
    private final Deque<Video> feed = new ArrayDeque<>();
    private final int FEED_CAP = 10;

    public YouTubeChannel(String name) { this.name = name; }

    @Override public void subscribe(Subscriber s) { subscribers.add(s); }
    @Override public void unsubscribe(Subscriber s) { subscribers.remove(s); }

    @Override public synchronized void upload(Video video) {
        if (feed.size() == FEED_CAP) feed.removeLast();
        feed.addFirst(video);
        notifyAllSubscribers(video);
    }

    private void notifyAllSubscribers(Video video) {
        // push the new video; failures are isolated per subscriber
        for (Subscriber s : subscribers) {
            try { s.update(video); } 
            catch (Exception e) { System.err.println("Notify failed for " + s.id() + ": " + e); }
        }
    }

    @Override public synchronized Video getLatestVideo() {
        return feed.peekFirst();
    }

    @Override public String toString() { return "YouTubeChannel{name='%s'}".formatted(name); }
}

// ----- Concrete Observers -----
class MobileSubscriber implements Subscriber {
    private final String userId;
    public MobileSubscriber(String userId) { this.userId = userId; }
    @Override public void update(Video video) {
        System.out.println("[Mobile] " + userId + " got: " + video);
    }
    @Override public String id() { return "mobile:" + userId; }
}

class EmailSubscriber implements Subscriber {
    private final String email;
    public EmailSubscriber(String email) { this.email = email; }
    @Override public void update(Video video) {
        System.out.println("[Email]  " + email + " -> New video: " + video.title() + " (" + video.url() + ")");
    }
    @Override public String id() { return "email:" + email; }
}

// ----- Demo / Driver -----
public class YoutubeObserverDemo {
    public static void main(String[] args) {
        Channel tech = new YouTubeChannel("TechWithA");

        Subscriber s1 = new MobileSubscriber("alice");
        Subscriber s2 = new EmailSubscriber("bob@example.com");
        Subscriber s3 = new MobileSubscriber("charlie");

        tech.subscribe(s1);
        tech.subscribe(s2);
        tech.subscribe(s3);

        tech.upload(new Video("v1", "Observer Pattern in Java", "https://yt/abc"));
        tech.upload(new Video("v2", "Java Concurrency in Practice - Summary", "https://yt/def"));

        tech.unsubscribe(s3);
        tech.upload(new Video("v3", "Design Patterns Speedrun", "https://yt/ghi"));

        System.out.println("Latest: " + tech.getLatestVideo());
    }
}

/*
Notes to say in interview

CopyOnWriteArrayList avoids ConcurrentModificationException while notifying.

upload is synchronized to keep feed consistent; notification itself is outside the lock’s critical work.

In prod: queue notifications (async), add retry/DLQ, and persist subscriptions.
*/
