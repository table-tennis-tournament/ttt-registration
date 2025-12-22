package com.tt.tournament.xmlimport.application

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class XmlParserServiceTest {

    private lateinit var xmlParserService: XmlParserService

    @BeforeEach
    fun setUp() {
        xmlParserService = XmlParserService()
    }

    @Test
    fun `Given valid tournament XML when parsing then return TournamentDto with competitions`() {
        // given
        val xmlContent = File("src/test/resources/tournamentExport_samstag.xml").readText()

        // when
        val result = xmlParserService.parseTournamentXml(xmlContent)

        // then
        Assertions.assertThat(result.name).contains("Albgau")
        Assertions.assertThat(result.competitions).isNotEmpty
    }

    @Test
    fun `Given tournament XML with players when parsing then players are correctly mapped`() {
        // given
        val xmlContent = File("src/test/resources/tournamentExport_samstag.xml").readText()

        // when
        val result = xmlParserService.parseTournamentXml(xmlContent)

        // then
        val senioren40Competition = result.competitions.find {
            it.ageGroup.equals("Senioren 40") && it.type.equals("Einzel")
        }
        Assertions.assertThat(senioren40Competition).isNotNull
        Assertions.assertThat(senioren40Competition?.players?.players).isNotEmpty

        val firstPlayer = senioren40Competition?.players?.players?.get(0)
        Assertions.assertThat(firstPlayer?.person?.licenceNr).isEqualTo("1080")
        Assertions.assertThat(firstPlayer?.person?.firstname).isEqualTo("Thomas")
        Assertions.assertThat(firstPlayer?.person?.lastname).isEqualTo("Fritsche")
    }

    @Test
    fun `Given invalid XML when parsing then throw XmlParseException`() {
        // given
        val invalidXml = "<invalid>xml</invalid>"

        // when / then
        Assertions.assertThatThrownBy {
            xmlParserService.parseTournamentXml(invalidXml)
        }
            .isInstanceOf(XmlParseException::class.java)
            .hasMessageContaining("Invalid tournament XML format")
    }
}