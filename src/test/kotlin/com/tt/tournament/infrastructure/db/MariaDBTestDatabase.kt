package com.tt.tournament.infrastructure.db

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.MariaDBContainer

@TestConfiguration(proxyBeanMethods = false)
class MariaDBTestDatabase {

    @Bean
    @ServiceConnection // This automatically injects the JDBC URL, username, and password
    fun mariadbContainer(): MariaDBContainer<*> {
        return MariaDBContainer("mariadb:latest")
    }
}
