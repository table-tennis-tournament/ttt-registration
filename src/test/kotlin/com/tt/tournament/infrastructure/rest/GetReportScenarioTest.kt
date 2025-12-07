package com.tt.tournament.infrastructure.rest

import com.tt.tournament.infrastructure.db.H2TestDatabase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
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
    @Autowired val restTemplate: TestRestTemplate
) {

    @Test
    @Disabled("Fix sunday and saturday separation")
    fun `Assert sunday report created and loaded`() {
        val entity = restTemplate.getForEntity("/sunday-report", String::class.java)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isNull()
    }

    @Test
    fun `Assert saturday report created and loaded`() {
        val entity = restTemplate.getForEntity("/saturday-report", String::class.java)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isNull()
    }

    @Test
    fun `Assert lists report created and loaded`() {
        val entity = restTemplate.getForEntity("/player-lists", String::class.java)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isNull()
    }

}
