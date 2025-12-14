package com.tt.tournament.infrastructure.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class PasswordEncrypterTest {

    private val passwordEncoder = BCryptPasswordEncoder()

    @Test
    fun `Given password string when encoding then return valid BCrypt hash`() {
        // given
        val password = "mySecretPassword123"

        // when
        val hashedPassword = passwordEncoder.encode(password)

        // then
        assertThat(hashedPassword).isNotNull()
        assertThat(hashedPassword).isNotEmpty()
        assertThat(hashedPassword).startsWith("\$2a\$") // BCrypt hash prefix
        assertThat(hashedPassword?.length).isGreaterThan(50) // BCrypt hashes are typically 60 chars
    }

    @Test
    fun `Given same password when encoding twice then produce different hashes`() {
        // given
        val password = "myTest"

        // when
        val hash1 = passwordEncoder.encode(password)
        val hash2 = passwordEncoder.encode(password)

        // then - hashes should be different due to random salt
        assertThat(hash1).isNotEqualTo(hash2)
    }

    @Test
    fun `Given password and hash when matching correct password then return true`() {
        // given
        val password = "mySecretPassword123"
        val hashedPassword = passwordEncoder.encode(password)

        // when
        val matches = passwordEncoder.matches(password, hashedPassword)

        // then
        assertThat(matches).isTrue()
    }

    @Test
    fun `Given password and hash when matching wrong password then return false`() {
        // given
        val correctPassword = "mySecretPassword123"
        val wrongPassword = "wrongPassword"
        val hashedPassword = passwordEncoder.encode(correctPassword)

        // when
        val matches = passwordEncoder.matches(wrongPassword, hashedPassword)

        // then
        assertThat(matches).isFalse()
    }

    @Test
    fun `Given special characters password when encoding then hash and verify correctly`() {
        // given
        val passwordWithSpecialChars = "P@ssw0rd!#\$%^&*()"

        // when
        val hashedPassword = passwordEncoder.encode(passwordWithSpecialChars)

        // then
        assertThat(hashedPassword).isNotNull()
        assertThat(passwordEncoder.matches(passwordWithSpecialChars, hashedPassword)).isTrue()
    }
}