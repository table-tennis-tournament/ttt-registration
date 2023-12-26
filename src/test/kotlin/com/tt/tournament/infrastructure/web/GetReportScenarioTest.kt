package com.tt.tournament.infrastructure.web

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetReportScenarioTest(@Autowired val restTemplate: TestRestTemplate) {

    @Test
    //@Sql("/db/create-tables.sql")
    fun `Assert sunday report created and loaded`() {
        val entity = restTemplate.getForEntity("/sunday-report", String::class.java)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isNull()
    }

    @Test
    //@Sql("/db/create-tables.sql")
    fun `Assert saturday report created and loaded`() {
        val entity = restTemplate.getForEntity("/saturday-report", String::class.java)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isNull()
    }

}
