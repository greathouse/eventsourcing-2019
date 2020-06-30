package greenmoonsoftware.es.event.jdbcstore;

import greenmoonsoftware.es.event.Event;
import greenmoonsoftware.es.event.EventSubscriber;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PackageOnlyJdbcStoreEventSubscriber extends JdbcStorePersister implements EventSubscriber {
    private final List<Package> packages;

    public PackageOnlyJdbcStoreEventSubscriber(JdbcStoreConfiguration config, DataSource ds, EventSerializer<Event> s, Package... packages) {
        super(config, ds, s);
        this.packages = Arrays.asList(packages);
    }

    public PackageOnlyJdbcStoreEventSubscriber(JdbcStoreConfiguration config, DataSource ds, Package... packages) {
        super(config, ds);
        this.packages = Arrays.asList(packages);
    }

    @Override
    protected boolean shouldHandle(Event event) {
        return packages.contains(event.getClass().getPackage());
    }

    @Override
    public void onEvent(Event event) {
        persist(Collections.singleton(event));
    }
}
