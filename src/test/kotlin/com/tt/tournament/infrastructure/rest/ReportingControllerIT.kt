package com.tt.tournament.infrastructure.rest

import com.tt.tournament.infrastructure.db.MariaDBTestDatabase
import com.tt.tournament.infrastructure.db.TestDataCreator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Testcontainers
@Import(MariaDBTestDatabase::class)
class ReportingControllerIT(
    @param:Autowired val restTemplate: TestRestTemplate,
    @param:Autowired val testDataCreator: TestDataCreator
) {

    private val authenticatedClient = restTemplate.withBasicAuth("admin", "password")

    @Test
    fun `should generate sunday report successfully`() {
        val entity = authenticatedClient.getForEntity("/sunday-report", String::class.java)

        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isNull()
    }

    @Test
    fun `should generate saturday report successfully`() {
        val entity = authenticatedClient.getForEntity("/saturday-report", String::class.java)

        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isNotNull()
    }

    @Test
    fun `should generate player lists successfully`() {
        val entity = authenticatedClient.getForEntity("/player-lists", String::class.java)

        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isNull()
    }
}
