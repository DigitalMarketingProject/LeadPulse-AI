package com.marketing.leadscore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:postgres_schema_validation;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/production-schema-postgresql.sql",
        "leadpulse.bootstrap-key=test-bootstrap-key",
        "leadpulse.admin.username=admin",
        "leadpulse.admin.password-bcrypt=$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
        "leadpulse.cors.allowed-origins=https://company.example"
})
@ActiveProfiles("production")
class PostgreSqlProductionSchemaTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void productionSchemaMatchesJpaEntities() throws SQLException {
        try (var connection = dataSource.getConnection()) {
            assertTrue(connection.isValid(1));
        }
    }
}
