package com.tt.tournament.infrastructure.db

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class TestDataCreator {

    private val logger = LoggerFactory.getLogger(TestDataCreator::class.java)

    @Autowired
    private lateinit var dataSource: DataSource

    @PostConstruct
    fun initializeDatabase() {
        logger.info("Initializing MariaDB test database...")
        try {
            val populator = ResourceDatabasePopulator()
            populator.addScript(ClassPathResource("db/create-tables.sql"))
            populator.setSeparator(";")
            populator.setIgnoreFailedDrops(true)
            populator.setContinueOnError(true)
            populator.execute(dataSource)
            logger.info("MariaDB test database initialized successfully")
        } catch (e: Exception) {
            logger.error("Failed to initialize MariaDB test database", e)
            throw e
        }
    }
}