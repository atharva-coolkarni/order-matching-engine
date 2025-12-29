package engine.queue;

import engine.events.Event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EventQueue {

    private final BlockingQueue<Event> queue = new LinkedBlockingQueue<>();

    public void publish(Event event) {
        queue.offer(event);
    }

    public Event take() throws InterruptedException {
        return queue.take();
    }
}
