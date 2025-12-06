package com.tt.tournament.infrastructure.db

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import javax.sql.DataSource

@TestConfiguration
class H2TestDatabase {

    private val logger = LoggerFactory.getLogger(H2TestDatabase::class.java)

    @Autowired
    private lateinit var dataSource: DataSource

    @PostConstruct
    fun startDatabase() {
        logger.info("Initializing H2 test database...")
        try {
            // Execute the create-tables.sql script on the existing datasource
            val populator = ResourceDatabasePopulator()
            populator.addScript(ClassPathResource("db/create-tables.sql"))
            populator.setSeparator(";")
            populator.setIgnoreFailedDrops(true)
            populator.setContinueOnError(true)
            populator.execute(dataSource)
            logger.info("H2 test database initialized successfully")
        } catch (e: Exception) {
            logger.error("Failed to initialize H2 test database", e)
            throw e
        }
    }

    @PreDestroy
    fun shutdownDatabase() {
        logger.info("Shutting down H2 test database...")
        // Database will be shut down automatically by Spring
    }
}
