package com.tt.tournament.xmlimport.infrastructure.database

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PlayerRepositoryExtensionTest {

    @Autowired
    private lateinit var playerRepository: PlayerRepository

    @Test
    fun `Given license number when finding player then return PlayerEntity if exists`() {
        // given - test data has players with license numbers
        val licenseNr = "08013229|"  // David Adkins from test data

        // when
        val result = playerRepository.findByLicenseNr(licenseNr)

        // then
        Assertions.assertThat(result).isNotNull
        Assertions.assertThat(result?.licenseNr).isEqualTo(licenseNr)
        Assertions.assertThat(result?.firstName).isEqualTo("David")
        Assertions.assertThat(result?.lastName).isEqualTo("Adkins")
    }

    @Test
    fun `Given non-existent license number when finding player then return null`() {
        // given
        val licenseNr = "NONEXISTENT999"

        // when
        val result = playerRepository.findByLicenseNr(licenseNr)

        // then
        Assertions.assertThat(result).isNull()
    }

    @Test
    fun `Given player details when creating then return new player ID and player is persisted`() {
        // given
        val firstName = "Test"
        val lastName = "Player"
        val licenseNr = "TEST${System.currentTimeMillis()}"
        val clubId = 1
        val sex = "M"
        val nationality = "DE"
        val ttr = 1500.0
        val birthYear = "1990"

        // when
        val playerId = playerRepository.createPlayer(
            firstName, lastName, licenseNr, clubId, sex, nationality, ttr, birthYear
        )

        // then
        Assertions.assertThat(playerId).isGreaterThan(0)

        val createdPlayer = playerRepository.findByLicenseNr(licenseNr)
        Assertions.assertThat(createdPlayer).isNotNull
        Assertions.assertThat(createdPlayer?.id).isEqualTo(playerId)
        Assertions.assertThat(createdPlayer?.firstName).isEqualTo(firstName)
        Assertions.assertThat(createdPlayer?.lastName).isEqualTo(lastName)
    }

    @Test
    fun `Given existing player when updating then player data is changed`() {
        // given - find existing player
        val licenseNr = "08013229|"
        val existingPlayer = playerRepository.findByLicenseNr(licenseNr)
        Assertions.assertThat(existingPlayer).isNotNull

        val newFirstName = "UpdatedFirstName"
        val newLastName = "UpdatedLastName"

        // when
        val rowsUpdated = playerRepository.updatePlayer(
            playerId = existingPlayer!!.id,
            firstName = newFirstName,
            lastName = newLastName,
            clubId = existingPlayer.clubId,
            sex = existingPlayer.sex,
            nationality = existingPlayer.nationality,
            ttr = existingPlayer.ttr,
            birthYear = "2002"
        )

        // then
        Assertions.assertThat(rowsUpdated).isEqualTo(1)

        val updatedPlayer = playerRepository.findByLicenseNr(licenseNr)
        Assertions.assertThat(updatedPlayer?.firstName).isEqualTo(newFirstName)
        Assertions.assertThat(updatedPlayer?.lastName).isEqualTo(newLastName)
    }

    @Test
    fun `Given player and type when checking enrollment then return true if enrolled`() {
        // given - from test data, player 54 exists and might be enrolled in a type
        // Let's create a known enrollment first
        val player = playerRepository.findByLicenseNr("08013229|")
        Assertions.assertThat(player).isNotNull

        // Assuming type 1 exists in test data
        val typeId = 1

        // Enroll the player first
        playerRepository.enrollPlayerInType(player!!.id, typeId, 0)

        // when
        val isEnrolled = playerRepository.isPlayerEnrolledInType(player.id, typeId)

        // then
        Assertions.assertThat(isEnrolled).isTrue()
    }

    @Test
    fun `Given player not enrolled in type when checking enrollment then return false`() {
        // given
        val player = playerRepository.findByLicenseNr("08013229|")
        Assertions.assertThat(player).isNotNull

        // Use a type ID that definitely doesn't exist
        val nonExistentTypeId = 99999

        // when
        val isEnrolled = playerRepository.isPlayerEnrolledInType(player!!.id, nonExistentTypeId)

        // then
        Assertions.assertThat(isEnrolled).isFalse()
    }

    @Test
    fun `Given player and type when enrolling then creates typeperplayer record`() {
        // given
        val player = playerRepository.findByLicenseNr("08013229|")
        Assertions.assertThat(player).isNotNull

        val typeId = 2  // Assuming type 2 exists

        // when
        val rowsInserted = playerRepository.enrollPlayerInType(player!!.id, typeId, 1)

        // then
        Assertions.assertThat(rowsInserted).isEqualTo(1)

        val isEnrolled = playerRepository.isPlayerEnrolledInType(player.id, typeId)
        Assertions.assertThat(isEnrolled).isTrue()
    }
}