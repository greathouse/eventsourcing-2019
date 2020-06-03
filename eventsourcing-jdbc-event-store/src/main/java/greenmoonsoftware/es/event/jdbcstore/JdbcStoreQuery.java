package greenmoonsoftware.es.event.jdbcstore;

import greenmoonsoftware.es.event.Aggregate;
import greenmoonsoftware.es.event.Event;
import greenmoonsoftware.es.event.EventList;
import greenmoonsoftware.es.store.EventRetrieval;
import greenmoonsoftware.es.store.StoreRetrieval;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public abstract class JdbcStoreQuery<T extends Aggregate> implements StoreRetrieval<T>, EventRetrieval {
    private final EventSerializer serializer;
    private final DataSource datasource;
    private final JdbcStoreConfiguration configuration;
    private final EventRetrieval eventRetrieval;

    public JdbcStoreQuery(
            JdbcStoreConfiguration config,
            DataSource ds,
            EventSerializer s
    ) {
        configuration = config;
        datasource = ds;
        serializer = s;
        eventRetrieval = new JdbcStoreEventRetrieval(configuration, datasource, serializer);
    }

    public JdbcStoreQuery(
            JdbcStoreConfiguration config,
            DataSource ds
    ) {
        this(config, ds, new ObjectEventSerializer());
    }

    protected abstract T create();

    public T retrieve(String aggregateId) {
        ensureNotNull(aggregateId);
        T aggregate = create();
        try (Connection con = datasource.getConnection();
             PreparedStatement ps = con.prepareStatement("select * from " + configuration.getTablename() + " where aggregateId = ? order by eventDateTime asc")
        ) {
            prepareExecuteAndApplyEvents(ps, aggregateId, aggregate);
        } catch (SQLException | ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        return aggregate;
    }

    @Override
    public Event retrieve(UUID id) {
        return eventRetrieval.retrieve(id);
    }

    private void ensureNotNull(String obj) {
        if (obj == null || "".equals(obj.trim())) {
            throw new IllegalArgumentException("Argument cannot be null or empty");
        }
    }

    private void prepareExecuteAndApplyEvents(PreparedStatement ps, String aggregateId, T aggregate) throws SQLException, IOException, ClassNotFoundException {
        ps.setString(1, aggregateId);
        EventList events = new EventList();
        try (ResultSet rs = ps.executeQuery()) {
            while(rs.next()) {
                events.add(deserialize(rs));
            }
        }
        aggregate.apply(events);
    }

    private Event deserialize(ResultSet rs) throws IOException, SQLException {
        return serializer.deserialize(rs.getString("eventType"), rs.getBinaryStream("data"));
    }
}
