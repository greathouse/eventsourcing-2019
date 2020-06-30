package greenmoonsoftware.es.event.jdbcstore;

import greenmoonsoftware.es.event.Event;
import greenmoonsoftware.es.store.StorePersister;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;

public abstract class JdbcStorePersister implements StorePersister {
    protected final EventSerializer<Event> serilalizer;
    protected DataSource datasource;
    protected JdbcStoreConfiguration configuration;

    public JdbcStorePersister(
            JdbcStoreConfiguration config, DataSource ds, EventSerializer<Event> s) {
        serilalizer = s;
        datasource = ds;
        configuration = config;
    }

    public JdbcStorePersister(
            JdbcStoreConfiguration config,
            DataSource ds) {
        this(config, ds, new ObjectEventSerializer());
    }

    @Override
    public void persist(Collection<Event> events) {
        String sql = "insert into " + configuration.getTablename() + " (id, aggregateId, eventType, eventDateTime, data) " +
                "values (?,?,?,?,?)";
        try (Connection con = datasource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)
        ) {
            con.setAutoCommit(false);
            for (Event event : events) {
                prepareAndExecuteStatement(event, ps);
            }
            con.commit();
        }
        catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    @Deprecated
    public void persist(Event event) {
        persist(Collections.singleton(event));
    }

    protected boolean shouldHandle(Event event) {
        return true;
    }

    private void prepareAndExecuteStatement(Event event, PreparedStatement ps) throws SQLException, IOException {
        if (!shouldHandle(event)) {
            return ;
        }
        ps.setString(1, event.getId().toString());
        ps.setString(2, event.getAggregateId());
        ps.setString(3, event.getType());
        ps.setTimestamp(4, new Timestamp(event.getEventDateTime().toEpochMilli()));
        ps.setBinaryStream(5, serilalizer.serialize(event));
        ps.execute();
    }
}
