package com.tt.tournament.accounting.infrastructure.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class PaymentControllerTest {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Test
    fun `Given Spring context when loading then PaymentController bean exists`() {
        // given - Spring Boot application context

        // when - checking for PaymentController bean
        val hasBean = applicationContext.containsBean("paymentController")

        // then - PaymentController should be registered
        assertThat(hasBean).isTrue()
    }

    @Test
    fun `Given Spring context when loading then can autowire PaymentController`() {
        // given - Spring Boot application context

        // when - getting PaymentController bean
        val controller = applicationContext.getBean(PaymentController::class.java)

        // then - PaymentController should not be null
        assertThat(controller).isNotNull
    }
}
