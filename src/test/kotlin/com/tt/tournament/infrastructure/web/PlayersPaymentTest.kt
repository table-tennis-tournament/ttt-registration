package com.tt.tournament.infrastructure.web

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ActiveProfiles
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
    fun `Given authenticated user when update payment status then returns success`() {
        val requestBody = """
            {
                "playerId": 1,
                "paid": true
            }
        """.trimIndent()

        val perform = mockMvc.perform(post("/players/payment").content(requestBody))
        perform.andExpect(status().isOk)

        mockMvc.perform(
            post("/players/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(user("admin").password("password")) // 2. Mock the User
                .with(csrf()) // 3. Automatically inject valid CSRF token
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Payment status updated successfully"))
    }
}