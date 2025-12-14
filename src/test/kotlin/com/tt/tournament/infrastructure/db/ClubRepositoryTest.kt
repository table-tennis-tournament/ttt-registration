package com.tt.tournament.infrastructure.db

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ClubRepositoryTest {

    @Autowired
    private lateinit var clubRepository: ClubRepository

    @Test
    fun `Given club name when finding by name then return Club if exists`() {
        // given - test data has TTV Erdmannhausen
        val clubName = "TTV Erdmannhausen"

        // when
        val result = clubRepository.findByName(clubName)

        // then
        assertThat(result).isNotNull
        assertThat(result?.name).isEqualTo(clubName)
        assertThat(result?.id).isGreaterThan(0)
    }

    @Test
    fun `Given non-existent club name when finding by name then return null`() {
        // given
        val clubName = "Non Existent Club XYZ 12345"

        // when
        val result = clubRepository.findByName(clubName)

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `Given club details when creating then return new club ID and club is persisted`() {
        // given
        val clubName = "Test Club ${System.currentTimeMillis()}"
        val verband = "TEST"
        val clubNr = "999"

        // when
        val clubId = clubRepository.create(clubName, verband, clubNr)

        // then
        assertThat(clubId).isGreaterThan(0)

        val createdClub = clubRepository.findByName(clubName)
        assertThat(createdClub).isNotNull
        assertThat(createdClub?.id).isEqualTo(clubId)
        assertThat(createdClub?.verband).isEqualTo(verband)
        assertThat(createdClub?.clubNr).isEqualTo(clubNr)
    }
}
