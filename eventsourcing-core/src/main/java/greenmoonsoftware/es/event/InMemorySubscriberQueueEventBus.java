package greenmoonsoftware.es.event;

import greenmoonsoftware.es.Bus;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemorySubscriberQueueEventBus implements Bus<Event, EventSubscriber> {
    private final Map<EventSubscriber,BlockingQueue<Event>> subscriberToQueue = new HashMap<>();
    private final Map<EventSubscriber,Thread> subscriberToThread = new HashMap<>();
    private final AtomicInteger threadCounter = new AtomicInteger(0);

    @Override
    public void post(Event payload) {
        subscriberToQueue.values().forEach(x -> x.offer(payload));
    }

    @Override
    public Bus<Event, EventSubscriber> register(EventSubscriber subscriber) {
        LinkedBlockingQueue<Event> queue = new LinkedBlockingQueue<>();
        subscriberToQueue.put(subscriber, queue);
        Thread t = new Thread(new EventSubscriberRunnable(subscriber, queue), "evtsub-" + threadCounter.getAndIncrement() + "-" + subscriber.getClass().getSimpleName());
        t.start();
        subscriberToThread.put(subscriber, t);
        return this;
    }

    @Override
    public void unregister(EventSubscriber subscriber) {
        subscriberToQueue.remove(subscriber);
        subscriberToThread.remove(subscriber);
    }

    private static class EventSubscriberRunnable implements Runnable {
        private final String label;
        private final EventSubscriber subscriber;
        private final BlockingQueue<Event> queue;
        private volatile boolean keepRunning = true;
        private EventSubscriberRunnable(EventSubscriber subscriber, BlockingQueue<Event> queue) {
            this.label = subscriber.getClass().getSimpleName();
            this.subscriber = subscriber;
            this.queue = queue;
        }
        @Override
        public void run() {
            while(keepRunning) {
                try {
                    subscriber.onEvent(queue.take());
                }  catch (Throwable t) {
                    System.out.println(label + ": Caught Throwable");
                    t.printStackTrace();
                }
            }
        }
    }
}