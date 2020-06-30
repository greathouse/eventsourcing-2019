package greenmoonsoftware.es.event.jdbcstore;

import greenmoonsoftware.es.event.Event;
import greenmoonsoftware.es.event.EventSubscriber;

import javax.sql.DataSource;

@Deprecated
public class JdbcStoreEventSubscriber extends JdbcStorePersister implements EventSubscriber<Event> {

    public JdbcStoreEventSubscriber(
            JdbcStoreConfiguration config,
            DataSource ds,
            EventSerializer<Event> s) {
        super(config, ds, s);
    }

    public JdbcStoreEventSubscriber(
            JdbcStoreConfiguration config,
            DataSource ds) {
        this(config, ds, new ObjectEventSerializer());
    }

    @Override
    public void onEvent(Event event) {
        persist(event);
    }

}
