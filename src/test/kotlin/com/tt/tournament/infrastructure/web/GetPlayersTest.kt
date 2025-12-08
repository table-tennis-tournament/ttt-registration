package com.tt.tournament.infrastructure.web

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import java.util.Base64

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class GetPlayersTest {

    @Autowired
    private lateinit var restTestClient: TestRestTemplate

    private fun extractCsrfToken(html: String): Pair<String, String>? {
        val tokenRegex = """<meta name="_csrf" content="([^"]+)"""".toRegex()
        val headerRegex = """<meta name="_csrf_header" content="([^"]+)"""".toRegex()

        val tokenMatch = tokenRegex.find(html)
        val headerMatch = headerRegex.find(html)

        return if (tokenMatch != null && headerMatch != null) {
            Pair(headerMatch.groupValues[1], tokenMatch.groupValues[1])
        } else {
            null
        }
    }

    @Test
    fun `Given authenticated user when get players page then returns html with all players`() {
        // given - application is running with authenticated user
        val authenticatedClient = restTestClient.withBasicAuth("admin", "password")

        // when - we request the players page
        val response = authenticatedClient.getForEntity("/players", String::class.java)

        // then - we get an HTML page with status 200
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains("Players Overview")
        assertThat(response.body).contains("All Registered Players")
        assertThat(response.body).contains("First Name")
        assertThat(response.body).contains("Last Name")
        assertThat(response.body).contains("Club")
        assertThat(response.body).contains("Disciplines")
        assertThat(response.body).contains("Payment Amount")
        assertThat(response.body).contains("Paid")
    }

    @Test
    fun `Given unauthenticated user when get players page then redirects to login`() {
        // given - application is running without authentication

        // when - we request the players page
        val response = restTestClient.getForEntity("/players", String::class.java)

        // then - we get redirected to login page
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains("Login")
    }

    @Test
    fun `Given authenticated user when sorting by firstName ascending then returns sorted players`() {
        // given - application is running with authenticated user
        val authenticatedClient = restTestClient.withBasicAuth("admin", "password")

        // when - we request the players page with sorting by firstName ascending
        val response = authenticatedClient.getForEntity("/players?sortBy=firstName&order=asc", String::class.java)

        // then - we get an HTML page with sorted players
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains("Players Overview")
        assertThat(response.body).contains("▲") // ascending arrow
    }

    @Test
    fun `Given authenticated user when sorting by lastName descending then returns sorted players`() {
        // given - application is running with authenticated user
        val authenticatedClient = restTestClient.withBasicAuth("admin", "password")

        // when - we request the players page with sorting by lastName descending
        val response = authenticatedClient.getForEntity("/players?sortBy=lastName&order=desc", String::class.java)

        // then - we get an HTML page with sorted players
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains("Players Overview")
        assertThat(response.body).contains("▼") // descending arrow
    }

    @Test
    fun `Given authenticated user when sorting by club then returns sorted players`() {
        // given - application is running with authenticated user
        val authenticatedClient = restTestClient.withBasicAuth("admin", "password")

        // when - we request the players page with sorting by club
        val response = authenticatedClient.getForEntity("/players?sortBy=club&order=asc", String::class.java)

        // then - we get an HTML page with sorted players
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains("Players Overview")
    }

    @Test
    fun `Given authenticated user when sorting by totalAmount then returns sorted players`() {
        // given - application is running with authenticated user
        val authenticatedClient = restTestClient.withBasicAuth("admin", "password")

        // when - we request the players page with sorting by totalAmount
        val response = authenticatedClient.getForEntity("/players?sortBy=totalAmount&order=asc", String::class.java)

        // then - we get an HTML page with sorted players
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains("Players Overview")
    }

    @Test
    fun `Given authenticated user when sorting by paid status then returns sorted players`() {
        // given - application is running with authenticated user
        val authenticatedClient = restTestClient.withBasicAuth("admin", "password")

        // when - we request the players page with sorting by paid status
        val response = authenticatedClient.getForEntity("/players?sortBy=paid&order=asc", String::class.java)

        // then - we get an HTML page with sorted players
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains("Players Overview")
    }

    @Test
    fun `Given authenticated user when get players page then contains filter input box`() {
        // given - application is running with authenticated user
        val authenticatedClient = restTestClient.withBasicAuth("admin", "password")

        // when - we request the players page
        val response = authenticatedClient.getForEntity("/players", String::class.java)

        // then - we get an HTML page with filter input box
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains("Filter by First Name, Last Name, or Club:")
        assertThat(response.body).contains("id=\"playerFilter\"")
        assertThat(response.body).contains("Type to filter players...")
        assertThat(response.body).contains("No players found matching your search")
    }

    @Test
    fun `Given authenticated user when get players page then contains CSRF token`() {
        // given - application is running with authenticated user
        val authenticatedClient = restTestClient.withBasicAuth("admin", "password")

        // when - we request the players page
        val response = authenticatedClient.getForEntity("/players", String::class.java)

        // then - we get an HTML page with CSRF token meta tags
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains("name=\"_csrf\"")
        assertThat(response.body).contains("name=\"_csrf_header\"")
    }

    @Test
    fun `Given authenticated user when update payment status to paid then returns success`() {
        // given - application is running with authenticated user
        val authenticatedClient = restTestClient.withBasicAuth("admin", "password")

        // First, get the CSRF token from the players page
        val pageResponse = authenticatedClient.getForEntity("/players", String::class.java)
        val (csrfHeader, csrfToken) = extractCsrfToken(pageResponse.body!!)!!

        // Prepare request with CSRF token and Basic Auth
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set(csrfHeader, csrfToken)
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:password".toByteArray()))

        val requestBody = mapOf("playerId" to 1, "paid" to true)
        val request = HttpEntity(requestBody, headers)

        // when - we update payment status to paid
        val response = restTestClient.exchange("/players/payment", HttpMethod.POST, request, Map::class.java)

        // then - we get success response
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body!!["success"]).isEqualTo(true)
        assertThat(response.body!!["message"]).isEqualTo("Payment status updated successfully")
    }

    @Test
    fun `Given authenticated user when update payment status to unpaid then returns success`() {
        // given - application is running with authenticated user
        val authenticatedClient = restTestClient.withBasicAuth("admin", "password")

        // First, get the CSRF token from the players page
        val pageResponse = authenticatedClient.getForEntity("/players", String::class.java)
        val (csrfHeader, csrfToken) = extractCsrfToken(pageResponse.body!!)!!

        // Prepare request with CSRF token and Basic Auth
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set(csrfHeader, csrfToken)
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:password".toByteArray()))

        val requestBody = mapOf("playerId" to 1, "paid" to false)
        val request = HttpEntity(requestBody, headers)

        // when - we update payment status to unpaid
        val response = restTestClient.exchange("/players/payment", HttpMethod.POST, request, Map::class.java)

        // then - we get success response
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body!!["success"]).isEqualTo(true)
        assertThat(response.body!!["message"]).isEqualTo("Payment status updated successfully")
    }

    @Test
    fun `Given authenticated user when update payment status for non-existent player then returns not found`() {
        // given - application is running with authenticated user
        val authenticatedClient = restTestClient.withBasicAuth("admin", "password")

        // First, get the CSRF token from the players page
        val pageResponse = authenticatedClient.getForEntity("/players", String::class.java)
        val (csrfHeader, csrfToken) = extractCsrfToken(pageResponse.body!!)!!

        // Prepare request with CSRF token and Basic Auth
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set(csrfHeader, csrfToken)
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:password".toByteArray()))

        val requestBody = mapOf("playerId" to 99999, "paid" to true)
        val request = HttpEntity(requestBody, headers)

        // when - we update payment status for non-existent player
        val response = restTestClient.exchange("/players/payment", HttpMethod.POST, request, Map::class.java)

        // then - we get not found response
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.body).isNotNull
        assertThat(response.body!!["success"]).isEqualTo(false)
        assertThat(response.body!!["message"]).isEqualTo("Player not found")
    }

    @Test
    fun `Given unauthenticated user when update payment status then redirects to login`() {
        // given - application is running without authentication
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val requestBody = mapOf("playerId" to 1, "paid" to true)
        val request = HttpEntity(requestBody, headers)

        // when - we try to update payment status without authentication
        val response = restTestClient.postForEntity("/players/payment", request, String::class.java)

        // then - we get redirected or unauthorized (401/403) since not authenticated
        assertThat(response.statusCode.is4xxClientError || response.statusCode.is3xxRedirection).isTrue()
    }
}
