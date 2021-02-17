package ru.krista.fm.artemisreciver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class DataSourceInitializer {

    @Bean
    public DataSource dataSource() {
        DataSource dataSource;

            DriverManagerDataSource managerDataSource = new DriverManagerDataSource();
            managerDataSource.setDriverClassName("org.postgresql.Driver");
            managerDataSource.setUsername("postgres");
            managerDataSource.setPassword("postgres");
            managerDataSource.setUrl("jdbc:postgresql://localhost:5432/testdb");
            dataSource = managerDataSource;
            log.debug("Database configured from try/catch");

        return dataSource;
    }
}
