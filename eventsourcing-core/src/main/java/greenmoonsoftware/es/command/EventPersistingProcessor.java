package greenmoonsoftware.es.command;

import greenmoonsoftware.es.Bus;
import greenmoonsoftware.es.event.Aggregate;
import greenmoonsoftware.es.event.Event;
import greenmoonsoftware.es.event.EventSubscriber;
import greenmoonsoftware.es.store.StorePersister;
import greenmoonsoftware.es.store.StoreRetrieval;

import java.util.Collection;

public class EventPersistingProcessor<T extends Aggregate> {
    private final StoreRetrieval<T> store;
    private final Bus<Event, EventSubscriber> eventBus;
    private final StorePersister persister;

    public EventPersistingProcessor(
            StoreRetrieval<T> store,
            StorePersister persister,
            Bus<Event, EventSubscriber> eventBus) {
        this.store = store;
        this.eventBus = eventBus;
        this.persister = persister;
    }

    public void process(Command command) {
        Aggregate aggregate = store.retrieve(command.getAggregateId());
        Collection<Event> newEvents = apply(command, aggregate);
        persister.persist(newEvents);
        newEvents.forEach(eventBus::post);
    }

    private Collection<Event> apply(Command command, Aggregate aggregate) {
        return AggregateCommandApplier.apply(aggregate, command);
    }
}
