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
        Assertions.assertThat(result).isNotNull
        Assertions.assertThat(result?.name).isEqualTo(clubName)
        Assertions.assertThat(result?.id).isGreaterThan(0)
    }

    @Test
    fun `Given non-existent club name when finding by name then return null`() {
        // given
        val clubName = "Non Existent Club XYZ 12345"

        // when
        val result = clubRepository.findByName(clubName)

        // then
        Assertions.assertThat(result).isNull()
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
        Assertions.assertThat(clubId).isGreaterThan(0)

        val createdClub = clubRepository.findByName(clubName)
        Assertions.assertThat(createdClub).isNotNull
        Assertions.assertThat(createdClub?.id).isEqualTo(clubId)
        Assertions.assertThat(createdClub?.verband).isEqualTo(verband)
        Assertions.assertThat(createdClub?.clubNr).isEqualTo(clubNr)
    }
}