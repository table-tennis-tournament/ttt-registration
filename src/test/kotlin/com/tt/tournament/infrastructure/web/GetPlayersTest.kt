package com.tt.tournament.infrastructure.web

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class GetPlayersTest {

    @Autowired
    private lateinit var restTestClient: TestRestTemplate

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
}
