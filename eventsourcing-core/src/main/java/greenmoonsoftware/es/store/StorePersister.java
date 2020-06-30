package greenmoonsoftware.es.store;

import greenmoonsoftware.es.event.Event;

import java.util.Collection;

public interface StorePersister {
    @Deprecated void persist(Event event);

    default void persist(Collection<Event> events) {
        events.forEach(this::persist);
    }
}
