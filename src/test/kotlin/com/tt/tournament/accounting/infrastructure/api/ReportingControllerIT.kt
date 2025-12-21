package com.tt.tournament.accounting.infrastructure.api

import com.tt.tournament.infrastructure.db.MariaDBTestDatabase
import com.tt.tournament.infrastructure.db.TestDataCreator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.resttestclient.getForEntity
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.collections.take
import kotlin.collections.toByteArray

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
        val entity = authenticatedClient.getForEntity<String>("/sunday-report")

        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isNull()
    }

    @Test
    fun `should generate saturday report successfully`() {
        val entity = authenticatedClient.getForEntity<String>("/saturday-report")

        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isNotNull()
    }

    @Test
    fun `should generate player lists successfully`() {
        val entity = authenticatedClient.getForEntity<String>("/player-lists")

        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isNotNull()
        assertThat(entity.body).isNotEmpty
        assertThat(entity.headers.contentType.toString()).contains("application/pdf")
        assertThat(entity.headers.contentDisposition.toString()).contains("attachment")
        assertThat(entity.headers.contentDisposition.toString()).contains("spielerliste.pdf")

        // verify PDF magic bytes
        val pdfMagicBytes = "%PDF-".toByteArray()
        val actualMagicBytes = entity.body!!.take(5).toByteArray()
        assertThat(actualMagicBytes).isEqualTo(pdfMagicBytes)
    }
}
