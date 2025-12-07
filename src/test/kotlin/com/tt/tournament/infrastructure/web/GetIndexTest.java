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
        // given - application is running
        String expectedHtml = """
                <!DOCTYPE html>
                <html lang="de">
                <head>
                    <title>Table Tennis Tournament</title>
                </head>
                <body>
                    <h1>Table Tennis Tournament Registration</h1>
                </body>
                </html>""";

        // when - we request the index page
        var response = restTestClient.getForEntity("/", String.class);

        // then - we get an HTML page with status 200
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToIgnoringWhitespace(expectedHtml);
    }
}
