package greenmoonsoftware.es.event.jdbcstore;

import greenmoonsoftware.es.event.Event;

import javax.sql.DataSource;

public class ChildEventOnlyJdbcStoreEventSubscriber extends JdbcStoreEventSubscriber {
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
}
