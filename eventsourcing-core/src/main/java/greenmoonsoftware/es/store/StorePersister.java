package greenmoonsoftware.es.store;

import greenmoonsoftware.es.event.Event;

public interface StorePersister {
    void persist(Event event);
}
