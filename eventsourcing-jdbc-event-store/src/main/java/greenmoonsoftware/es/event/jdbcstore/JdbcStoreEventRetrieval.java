package greenmoonsoftware.es.event.jdbcstore;

import greenmoonsoftware.es.event.Event;
import greenmoonsoftware.es.store.EventRetrieval;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class JdbcStoreEventRetrieval implements EventRetrieval {
    private final JdbcStoreConfiguration configuration;
    private final DataSource datasource;
    private final EventSerializer serializer;

    public JdbcStoreEventRetrieval(JdbcStoreConfiguration configuration,
                                   DataSource datasource,
                                   EventSerializer serializer) {
        this.configuration = configuration;
        this.datasource = datasource;
        this.serializer = serializer;
    }


    @Override
    public Event retrieve(UUID id) {
        ensureNotNull(id);
        try (Connection con = datasource.getConnection();
             PreparedStatement ps = con.prepareStatement("select * from " + configuration.getTablename() + " where id = ?")){
            ps.setString(1, id.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalStateException("No event exists with ID " + id + " in table " + configuration.getTablename());
                }
                return serializer.deserialize(rs.getString("eventType"), rs.getBinaryStream("data"));
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Unexpected exception retrieving event with ID " + id + " from table " + configuration.getTablename());
        }
    }

    private void ensureNotNull(Object obj) {
        if (obj == null || "".equals(obj.toString().trim())) {
            throw new IllegalArgumentException("Argument cannot be null or empty");
        }
    }
}
