package greenmoonsoftware.es.store;

import greenmoonsoftware.es.event.Event;

import java.util.UUID;

public interface EventRetrieval {
    Event retrieve(UUID id);
}
