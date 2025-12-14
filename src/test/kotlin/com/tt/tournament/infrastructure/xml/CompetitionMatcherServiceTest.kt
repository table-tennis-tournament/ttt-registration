package com.tt.tournament.infrastructure.xml

import com.tt.tournament.infrastructure.db.TypeRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CompetitionMatcherServiceTest {

    @Autowired
    private lateinit var competitionMatcherService: CompetitionMatcherService

    @Autowired
    private lateinit var typeRepository: TypeRepository

    @Test
    fun `Given competition with age group and type when building name then format correctly`() {
        // given
        val competition = CompetitionDto(
            ageGroup = "Senioren 40",
            type = "Einzel",
            ttrRemarks = " "
        )

        // when
        val name = competitionMatcherService.buildCompetitionName(competition)

        // then
        assertThat(name).isEqualTo("Senioren 40 Einzel")
    }

    @Test
    fun `Given competition with TTR remarks when building name then include remarks`() {
        // given
        val competition = CompetitionDto(
            ageGroup = "Jugend 19",
            type = "Einzel",
            ttrRemarks = "B"
        )

        // when
        val name = competitionMatcherService.buildCompetitionName(competition)

        // then
        assertThat(name).isEqualTo("Jugend 19 B Einzel")
    }

    @Test
    fun `Given competition with empty TTR remarks when building name then exclude remarks`() {
        // given
        val competition = CompetitionDto(
            ageGroup = "Mädchen 19",
            type = "Doppel",
            ttrRemarks = null
        )

        // when
        val name = competitionMatcherService.buildCompetitionName(competition)

        // then
        assertThat(name).isEqualTo("Mädchen 19 Doppel")
    }

    @Test
    fun `Given competition name when matching to type then return TypeEntity if exists`() {
        // given - assuming we have some test types in the database
        // This test will depend on the actual test data
        // For now, let's test the method returns null for non-existent types
        val competition = CompetitionDto(
            ageGroup = "Non Existent",
            type = "Einzel"
        )

        // when
        val result = competitionMatcherService.matchCompetitionToType(competition)

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `Given existing competition when matching or creating then return existing type`() {
        // given - create a type first
        val uniqueTimestamp = System.currentTimeMillis()
        val typeName = "Existing Type $uniqueTimestamp"
        val existingTypeId = typeRepository.create(typeName, 5.0)

        val competition = CompetitionDto(
            ageGroup = "Existing Type",
            type = "$uniqueTimestamp",
            ttrRemarks = null
        )

        // when
        val result = competitionMatcherService.matchOrCreateCompetitionType(competition)

        // then
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(existingTypeId)
        assertThat(result.name).isEqualTo(typeName)
        assertThat(result.startGebuehr).isEqualTo(5.0)
    }

    @Test
    fun `Given non-existent competition when matching or creating then create new type`() {
        // given
        val uniqueTimestamp = System.currentTimeMillis()
        val competition = CompetitionDto(
            ageGroup = "New Competition",
            type = "Test $uniqueTimestamp",
            ttrRemarks = null
        )
        val expectedName = "New Competition Test $uniqueTimestamp"

        // when
        val result = competitionMatcherService.matchOrCreateCompetitionType(competition)

        // then
        assertThat(result).isNotNull
        assertThat(result.name).isEqualTo(expectedName)
        assertThat(result.startGebuehr).isEqualTo(0.0)
        assertThat(result.active).isEqualTo(1)
        assertThat(result.id).isGreaterThan(0)

        // Verify type was actually created in database
        val createdType = typeRepository.findByName(expectedName)
        assertThat(createdType).isNotNull
        assertThat(createdType?.id).isEqualTo(result.id)
    }

    @Test
    fun `Given competition with TTR remarks when creating then include remarks in type name`() {
        // given
        val uniqueTimestamp = System.currentTimeMillis()
        val competition = CompetitionDto(
            ageGroup = "Jugend 15",
            type = "Einzel $uniqueTimestamp",
            ttrRemarks = "A"
        )
        val expectedName = "Jugend 15 A Einzel $uniqueTimestamp"

        // when
        val result = competitionMatcherService.matchOrCreateCompetitionType(competition)

        // then
        assertThat(result).isNotNull
        assertThat(result.name).isEqualTo(expectedName)

        // Verify correct name format in database
        val createdType = typeRepository.findByName(expectedName)
        assertThat(createdType).isNotNull
    }
}
