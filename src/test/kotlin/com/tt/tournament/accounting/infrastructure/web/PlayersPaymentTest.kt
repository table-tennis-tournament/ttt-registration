package com.tt.tournament.accounting.infrastructure.web

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlayersPaymentTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @Sql(scripts = ["/db/create-tables.sql"])
    fun `Given authenticated user when update payment status then returns success`() {
        val requestBody = """
            {
                "playerId": 54,
                "paid": true
            }
        """.trimIndent()

        mockMvc.perform(
            post("/players/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(user("admin").password("password"))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Payment status updated successfully"))
    }

    @Test
    fun `Given authenticated user when update payment status for non-existent player then returns not found`() {
        val requestBody = """
            {
                "playerId": 1,
                "paid": true
            }
        """.trimIndent()

        mockMvc.perform(
            post("/players/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(user("admin").password("password"))
                .with(csrf())
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Player not found"))


    }

    @Test
    fun `Given authenticated user when update payment status to unpaid then returns success`() {
        val requestBody = """
            {
                "playerId": 54,
                "paid": false
            }
        """.trimIndent()

        mockMvc.perform(
            post("/players/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(user("admin").password("password"))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Payment status updated successfully"))

    }
}