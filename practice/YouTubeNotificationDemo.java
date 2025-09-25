/*
### 1. Restate the problem

We want to simulate a YouTube notification system where users (subscribers) get notified when a YouTube channel uploads a new video. The system should use the Observer design pattern, where the channel is the subject and the users are observers.

---

### 2. Assumptions

- A YouTube channel can have multiple subscribers.
- When the channel uploads a new video, all subscribers get notified.
- Subscribers can subscribe or unsubscribe from a channel.
- Notifications can be a simple message like "New video uploaded: [video title]".
- We only need to implement the core Observer pattern logic, no UI or persistence.

---

### 3. Brute force approach

- Create a Channel class that maintains a list of subscribers.
- Create a Subscriber class that has a method to receive notifications.
- When a new video is uploaded, the Channel iterates over all subscribers and calls their notification method.

This is straightforward but tightly couples Channel and Subscriber classes.

---

### 4. Optimize

- Use Java's built-in Observer and Observable interfaces (deprecated since Java 9, so better to implement our own).
- Define interfaces for Subject (Channel) and Observer (Subscriber) to decouple them.
- This makes the system extensible and maintainable.

---

### 5. Edge cases

- No subscribers: uploading a video should not cause errors.
- Subscriber unsubscribes and then channel uploads a video: unsubscribed user should not get notified.
- Multiple channels and subscribers (optional extension).
- Subscriber subscribes multiple times (should be handled).

---

### 6. APIs/classes needed

- Interface `Observer` with method `update(String videoTitle)`
- Interface `Subject` with methods `subscribe(Observer)`, `unsubscribe(Observer)`, `notifySubscribers(String videoTitle)`
- Class `Channel` implements `Subject`
- Class `Subscriber` implements `Observer`

---

### 7. Architecture / Data model

- `Channel` maintains a list of `Observer` subscribers.
- `Subscriber` implements `update` method to receive notifications.
- When `Channel.uploadVideo(String videoTitle)` is called, it calls `notifySubscribers(videoTitle)`.

---

### 8. Dry run

- Create a channel "TechChannel".
- Create subscribers Alice and Bob.
- Alice and Bob subscribe to TechChannel.
- TechChannel uploads "Observer Pattern Tutorial".
- Both Alice and Bob receive notification.
- Bob unsubscribes.
- TechChannel uploads "Decorator Pattern Tutorial".
- Only Alice receives notification.

---

### 9. Final Java code

```java
*/
import java.util.ArrayList;
import java.util.List;

// Observer interface
interface Observer {
    void update(String channelName, String videoTitle);
}

// Subject interface
interface Subject {
    void subscribe(Observer observer);
    void unsubscribe(Observer observer);
    void notifySubscribers(String videoTitle);
}

// Concrete Subject
class Channel implements Subject {
    private String name;
    private List<Observer> subscribers;

    public Channel(String name) {
        this.name = name;
        this.subscribers = new ArrayList<>();
    }

    @Override
    public void subscribe(Observer observer) {
        if (!subscribers.contains(observer)) {
            subscribers.add(observer);
        }
    }

    @Override
    public void unsubscribe(Observer observer) {
        subscribers.remove(observer);
    }

    @Override
    public void notifySubscribers(String videoTitle) {
        for (Observer subscriber : subscribers) {
            subscriber.update(name, videoTitle);
        }
    }

    public void uploadVideo(String videoTitle) {
        System.out.println(name + " uploaded a new video: " + videoTitle);
        notifySubscribers(videoTitle);
    }
}

// Concrete Observer
class Subscriber implements Observer {
    private String name;

    public Subscriber(String name) {
        this.name = name;
    }

    @Override
    public void update(String channelName, String videoTitle) {
        System.out.println(name + " received notification: New video from " + channelName + " - " + videoTitle);
    }
}

// Demo
public class YouTubeNotificationDemo {
    public static void main(String[] args) {
        Channel techChannel = new Channel("TechChannel");

        Subscriber alice = new Subscriber("Alice");
        Subscriber bob = new Subscriber("Bob");

        techChannel.subscribe(alice);
        techChannel.subscribe(bob);

        techChannel.uploadVideo("Observer Pattern Tutorial");

        techChannel.unsubscribe(bob);

        techChannel.uploadVideo("Decorator Pattern Tutorial");
    }
}
