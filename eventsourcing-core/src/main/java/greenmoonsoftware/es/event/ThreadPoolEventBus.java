package greenmoonsoftware.es.event;

import greenmoonsoftware.es.Bus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolEventBus implements Bus<Event, EventSubscriber> {
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private List<EventSubscriber> subscribers = new ArrayList<>();

    @Override
    public void post(Event event) {
        subscribers.stream()
                .map(x -> (Runnable) () -> {
                    x.onEvent(event);
                })
                .forEach(executor::execute);
    }

    @Override
    public Bus<Event, EventSubscriber> register(EventSubscriber subscriber) {
        subscribers.add(subscriber);
        return this;
    }

    @Override
    public void unregister(EventSubscriber subscriber) {
        subscribers.remove(subscriber);
    }
}
