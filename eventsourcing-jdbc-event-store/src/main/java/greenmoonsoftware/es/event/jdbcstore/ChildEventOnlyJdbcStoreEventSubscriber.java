package greenmoonsoftware.es.event.jdbcstore;

import greenmoonsoftware.es.event.Event;
import greenmoonsoftware.es.event.EventSubscriber;

import javax.sql.DataSource;
import java.util.Collections;

public class ChildEventOnlyJdbcStoreEventSubscriber extends JdbcStorePersister implements EventSubscriber {
    private final Class parentClass;

    public ChildEventOnlyJdbcStoreEventSubscriber(JdbcStoreConfiguration config,
                                                  DataSource ds,
                                                  EventSerializer<Event> s, Class parentClass) {
        super(config, ds, s);
        this.parentClass = parentClass;
    }

    public ChildEventOnlyJdbcStoreEventSubscriber(JdbcStoreConfiguration config,
                                                  DataSource ds,
                                                  Class parentClass) {
        super(config, ds);
        this.parentClass = parentClass;
    }

    @Override
    protected boolean shouldHandle(Event event) {
        return parentClass.isAssignableFrom(event.getClass());
    }

    @Override
    public void onEvent(Event event) {
        persist(Collections.singleton(event));
    }
}
