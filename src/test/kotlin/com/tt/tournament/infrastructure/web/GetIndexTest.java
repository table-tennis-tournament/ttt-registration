package com.tt.tournament.infrastructure.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class GetIndexTest {

    @Autowired
    private TestRestTemplate restTestClient;

    @Test
    void givenApplicationStarted_whenGetIndex_thenReturnsHtmlPage() {
        // given - application is running with authenticated user
        TestRestTemplate authenticatedClient = restTestClient.withBasicAuth("admin", "password");

        // when - we request the index page
        var response = authenticatedClient.getForEntity("/", String.class);

        // then - we get an HTML page with status 200
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Table Tennis Tournament Registration");
        assertThat(response.getBody()).contains("Welcome,");
        assertThat(response.getBody()).contains("admin");
    }

    @Test
    void givenUnauthenticated_whenGetIndex_thenRedirectsToLogin() {
        // given - application is running without authentication

        // when - we request the index page
        var response = restTestClient.getForEntity("/", String.class);

        // then - we get redirected to login page
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Login");
    }
}
