package com.tt.tournament.infrastructure.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.tt.tournament.infrastructure.db.H2TestDatabase
import com.tt.tournament.infrastructure.db.PlayerRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.nio.file.Files
import java.nio.file.Paths

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(H2TestDatabase::class)
@TestPropertySource(properties = [
    "application.import.saturday=http://localhost:8089/saturday-export",
    "application.import.sunday=http://localhost:8089/sunday-export"
])
class AutomaticTournamentImportIT(
    @param:Autowired val mockMvc: MockMvc,
    @param:Autowired val playerRepository: PlayerRepository
) {

    private lateinit var wireMockServer: WireMockServer

    @BeforeEach
    fun setup() {
        wireMockServer = WireMockServer(WireMockConfiguration.options().port(8089))
        wireMockServer.start()
    }

    @AfterEach
    fun teardown() {
        wireMockServer.stop()
    }

    @Test
    fun `Given wiremock serves XML files when automatic import triggered then both files are imported successfully`() {
        // given - WireMock serves both tournament XML files
        val saturdayXml = loadTestXmlFile("tournamentExport_samstag.xml")
        val sundayXml = loadTestXmlFile("tournamentExport_Sonntag_2026.xml")

        wireMockServer.stubFor(
            get(urlEqualTo("/saturday-export"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml; charset=utf-8")
                        .withBody(saturdayXml)
                )
        )

        wireMockServer.stubFor(
            get(urlEqualTo("/sunday-export"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml; charset=utf-8")
                        .withBody(sundayXml)
                )
        )

        // when - we trigger the automatic import
        mockMvc.perform(
            post("/import/automatic-tournament-import")
                .with(user("admin").password("password"))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Both Saturday and Sunday tournaments imported successfully"))

        // verify both WireMock endpoints were called
        wireMockServer.verify(getRequestedFor(urlEqualTo("/saturday-export")))
        wireMockServer.verify(getRequestedFor(urlEqualTo("/sunday-export")))

        // verify data was imported (players should exist in database)
        val allPlayers = playerRepository.readAllPlayers()
        assertThat(allPlayers).isNotEmpty()
    }

    @Test
    fun `Given saturday endpoint fails when automatic import triggered then returns partial success`() {
        // given - Saturday endpoint fails, Sunday succeeds
        val sundayXml = loadTestXmlFile("tournamentExport_Sonntag_2026.xml")

        wireMockServer.stubFor(
            get(urlEqualTo("/saturday-export"))
                .willReturn(
                    aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")
                )
        )

        wireMockServer.stubFor(
            get(urlEqualTo("/sunday-export"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody(sundayXml)
                )
        )

        // when - we trigger the automatic import
        mockMvc.perform(
            post("/import/automatic-tournament-import")
                .with(user("admin").password("password"))
                .with(csrf())
        )
            .andExpect(status().isPartialContent)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errors").isNotEmpty)
    }

    @Test
    fun `Given sunday endpoint fails when automatic import triggered then returns partial success`() {
        // given - Saturday endpoint succeeds, Sunday fails
        val saturdayXml = loadTestXmlFile("tournamentExport_samstag.xml")

        wireMockServer.stubFor(
            get(urlEqualTo("/saturday-export"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody(saturdayXml)
                )
        )

        wireMockServer.stubFor(
            get(urlEqualTo("/sunday-export"))
                .willReturn(
                    aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")
                )
        )

        // when - we trigger the automatic import
        mockMvc.perform(
            post("/import/automatic-tournament-import")
                .with(user("admin").password("password"))
                .with(csrf())
        )
            .andExpect(status().isPartialContent)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errors").isNotEmpty)
            .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.containsString("Sunday")))
    }

    @Test
    fun `Given both endpoints fail when automatic import triggered then returns error response`() {
        // given - Both endpoints fail
        wireMockServer.stubFor(
            get(urlEqualTo("/saturday-export"))
                .willReturn(
                    aResponse()
                        .withStatus(404)
                        .withBody("Not Found")
                )
        )

        wireMockServer.stubFor(
            get(urlEqualTo("/sunday-export"))
                .willReturn(
                    aResponse()
                        .withStatus(404)
                        .withBody("Not Found")
                )
        )

        // when - we trigger the automatic import
        mockMvc.perform(
            post("/import/automatic-tournament-import")
                .with(user("admin").password("password"))
                .with(csrf())
        )
            .andExpect(status().isPartialContent)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errors").isNotEmpty)
    }

    @Test
    fun `Given invalid XML content when automatic import triggered then returns error response`() {
        // given - WireMock serves invalid XML
        wireMockServer.stubFor(
            get(urlEqualTo("/saturday-export"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<invalid>xml without tournament root</invalid>")
                )
        )

        wireMockServer.stubFor(
            get(urlEqualTo("/sunday-export"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<invalid>xml without tournament root</invalid>")
                )
        )

        // when - we trigger the automatic import
        mockMvc.perform(
            post("/import/automatic-tournament-import")
                .with(user("admin").password("password"))
                .with(csrf())
        )
            .andExpect(status().isPartialContent)
            .andExpect(jsonPath("$.success").value(false))
    }

    @Test
    fun `Given unauthenticated user when automatic import triggered then returns forbidden`() {
        // given - WireMock is set up but user is not authenticated
        val saturdayXml = loadTestXmlFile("tournamentExport_samstag.xml")
        wireMockServer.stubFor(
            get(urlEqualTo("/saturday-export"))
                .willReturn(aResponse().withStatus(200).withBody(saturdayXml))
        )

        // when - unauthenticated user tries to trigger import
        mockMvc.perform(
            post("/import/automatic-tournament-import")
        )
            .andExpect(status().isForbidden)
    }

    private fun loadTestXmlFile(filename: String): String {
        val resourcePath = Paths.get("src/test/resources/$filename")
        return Files.readString(resourcePath)
    }
}