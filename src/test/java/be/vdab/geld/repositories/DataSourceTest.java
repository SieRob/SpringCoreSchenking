package be.vdab.geld.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class DataSourceTest {
    private final DataSource dataSource;

    DataSourceTest(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Test
    void getConnection() throws SQLException {
        try (var con = dataSource.getConnection()){
            assertThat(con.getCatalog()).isEqualTo("geld");
        }
    }
}
