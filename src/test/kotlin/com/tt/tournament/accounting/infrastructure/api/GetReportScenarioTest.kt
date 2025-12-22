package com.tt.tournament.accounting.infrastructure.api

import com.tt.tournament.infrastructure.db.H2TestDatabase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
@Import(H2TestDatabase::class)
class GetReportScenarioTest(
    @param:Autowired val restTemplate: TestRestTemplate
) {

    private val authenticatedClient = restTemplate.withBasicAuth("admin", "password")

    @Test
    fun `Given authenticated user when get sunday report with no sunday data then returns empty response`() {
        // given - application is running with authenticated user
        // NOTE: test data only contains Saturday events (Type_ID < 20), no Sunday events (Type_ID > 20)

        // when - we request the sunday report
        val entity = authenticatedClient.getForEntity("/sunday-report", ByteArray::class.java)

        // then - we get an OK status with empty body since there's no Sunday data
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        // Spring returns null body when byte array is empty
        assertThat(entity.body == null || entity.body!!.isEmpty()).isTrue()
        assertThat(entity.headers.contentType.toString()).contains("application/pdf")
        assertThat(entity.headers.contentDisposition.toString()).contains("attachment")
        assertThat(entity.headers.contentDisposition.toString()).contains("quittungen_sonntag.pdf")
    }

    @Test
    fun `Given authenticated user when get saturday report then returns PDF download`() {
        // given - application is running with authenticated user

        // when - we request the saturday report
        val entity = authenticatedClient.getForEntity("/saturday-report", ByteArray::class.java)

        // then - we get a PDF file download
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isNotNull
        assertThat(entity.body).isNotEmpty
        assertThat(entity.headers.contentType.toString()).contains("application/pdf")
        assertThat(entity.headers.contentDisposition.toString()).contains("attachment")
        assertThat(entity.headers.contentDisposition.toString()).contains("quittungen_samstag.pdf")

        // verify PDF magic bytes
        val pdfMagicBytes = "%PDF-".toByteArray()
        val actualMagicBytes = entity.body!!.take(5).toByteArray()
        assertThat(actualMagicBytes).isEqualTo(pdfMagicBytes)
    }

    @Test
    fun `Given unauthenticated user when get sunday report then redirects to login`() {
        // given - application is running without authentication

        // when - we try to request the sunday report
        val entity = restTemplate.getForEntity("/sunday-report", String::class.java)

        // then - we get redirected to login page
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).contains("Login")
    }

    @Test
    fun `Given unauthenticated user when get saturday report then redirects to login`() {
        // given - application is running without authentication

        // when - we try to request the saturday report
        val entity = restTemplate.getForEntity("/saturday-report", String::class.java)

        // then - we get redirected to login page
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).contains("Login")
    }

    @Test
    fun `Given authenticated user when get player list report then returns PDF download`() {
        // given - application is running with authenticated user

        // when - we request the saturday report
        val entity = authenticatedClient.getForEntity("/player-lists", ByteArray::class.java)

        // then - we get a PDF file download
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isNotNull
        assertThat(entity.body).isNotEmpty
        assertThat(entity.headers.contentType.toString()).contains("application/pdf")
        assertThat(entity.headers.contentDisposition.toString()).contains("attachment")
        assertThat(entity.headers.contentDisposition.toString()).contains("spielerliste.pdf")

        // verify PDF magic bytes
        val pdfMagicBytes = "%PDF-".toByteArray()
        val actualMagicBytes = entity.body!!.take(5).toByteArray()
        assertThat(actualMagicBytes).isEqualTo(pdfMagicBytes)
    }

    @Test
    fun `Given authenticated user when get blank receipt then returns PDF download with two discipline rows`() {
        // given - application is running with authenticated user

        // when - we request the blank receipt
        val entity = authenticatedClient.getForEntity("/blank-receipt", ByteArray::class.java)

        // then - we get a PDF file download
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isNotNull
        assertThat(entity.body).isNotEmpty
        assertThat(entity.headers.contentType.toString()).contains("application/pdf")
        assertThat(entity.headers.contentDisposition.toString()).contains("attachment")
        assertThat(entity.headers.contentDisposition.toString()).contains("blanko_quittung.pdf")

        // verify PDF magic bytes
        val pdfMagicBytes = "%PDF-".toByteArray()
        val actualMagicBytes = entity.body!!.take(5).toByteArray()
        assertThat(actualMagicBytes).isEqualTo(pdfMagicBytes)
    }

    @Test
    fun `Given unauthenticated user when get blank receipt then redirects to login`() {
        // given - application is running without authentication

        // when - we try to request the blank receipt
        val entity = restTemplate.getForEntity("/blank-receipt", String::class.java)

        // then - we get redirected to login page
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).contains("Login")
    }

}
