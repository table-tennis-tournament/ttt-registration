package com.tt.tournament.infrastructure.db

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.utility.DockerImageName
import javax.sql.DataSource

@TestConfiguration
class MariaDBTestDatabase {

    private val logger = LoggerFactory.getLogger(MariaDBTestDatabase::class.java)

    @Autowired
    private lateinit var dataSource: DataSource

    companion object {
        private val mariaDBContainer: MariaDBContainer<*> = MariaDBContainer(
            DockerImageName.parse("mariadb:11.2")
        ).apply {
            withDatabaseName("ttvettlingen24")
            withUsername("test")
            withPassword("test")
            start()
        }

        init {
            System.setProperty("spring.datasource.url", mariaDBContainer.jdbcUrl)
            System.setProperty("spring.datasource.username", mariaDBContainer.username)
            System.setProperty("spring.datasource.password", mariaDBContainer.password)
        }
    }

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
