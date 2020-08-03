package greenmoonsoftware.es.event.jdbcstore;

import greenmoonsoftware.es.store.StorePurger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcStorePurger implements StorePurger {
    private final JdbcStoreConfiguration configuration;
    private final DataSource datasource;

    public JdbcStorePurger(JdbcStoreConfiguration configuration, DataSource datasource) {
        this.configuration = configuration;
        this.datasource = datasource;
    }


    @Override
    public void purge(String aggregateId) {
        ensureNotNull(aggregateId);
        try (Connection con = datasource.getConnection();
             PreparedStatement ps = con.prepareStatement("delete from " + configuration.getTablename() + " where aggregateId = ?")
        ) {
            ps.setString(1, aggregateId);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void ensureNotNull(String obj) {
        if (obj == null || "".equals(obj.trim())) {
            throw new IllegalArgumentException("Argument cannot be null or empty");
        }
    }

}
