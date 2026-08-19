package org.n52.project.enforce.geoquest.api.impl;

import org.n52.project.enforce.geoquest.utils.GeoquestUtils;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgisContainerProvider;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = Application.class)
@ComponentScan(basePackageClasses = { GeoquestUtils.class})
public abstract class DBTest {

    static JdbcDatabaseContainer<?> database = new PostgisContainerProvider().newInstance()
            .withDatabaseName("integration-tests-db").withUsername("sa").withPassword("sa");

    static {
        database.start();
    }

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.datasource.url", database::getJdbcUrl);
        propertyRegistry.add("spring.datasource.password", database::getPassword);
        propertyRegistry.add("spring.datasource.username", database::getUsername);
    }
}
