package com.tt.tournament.infrastructure.xml

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.io.File

@SpringBootTest
@ActiveProfiles("test")
class XmlParserServiceTest {

    @Autowired
    private lateinit var xmlParserService: XmlParserService

    @Test
    fun `Given valid tournament XML when parsing then return TournamentDto with competitions`() {
        // given
        val xmlContent = File("src/test/resources/tournamentExport_samstag.xml").readText()

        // when
        val result = xmlParserService.parseTournamentXml(xmlContent)

        // then
        assertThat(result.name).contains("Albgau")
        assertThat(result.competitions).isNotEmpty
    }

    @Test
    fun `Given tournament XML with players when parsing then players are correctly mapped`() {
        // given
        val xmlContent = File("src/test/resources/tournamentExport_samstag.xml").readText()

        // when
        val result = xmlParserService.parseTournamentXml(xmlContent)

        // then
        val senioren40Competition = result.competitions.find {
            it.ageGroup == "Senioren 40" && it.type == "Einzel"
        }
        assertThat(senioren40Competition).isNotNull
        assertThat(senioren40Competition?.players?.players).isNotEmpty

        val firstPlayer = senioren40Competition?.players?.players?.get(0)
        assertThat(firstPlayer?.person?.licenceNr).isEqualTo("1080")
        assertThat(firstPlayer?.person?.firstname).isEqualTo("Thomas")
        assertThat(firstPlayer?.person?.lastname).isEqualTo("Fritsche")
    }

    @Test
    fun `Given invalid XML when parsing then throw XmlParseException`() {
        // given
        val invalidXml = "<invalid>xml</invalid>"

        // when / then
        assertThatThrownBy {
            xmlParserService.parseTournamentXml(invalidXml)
        }
            .isInstanceOf(XmlParseException::class.java)
            .hasMessageContaining("Invalid tournament XML format")
    }
}
