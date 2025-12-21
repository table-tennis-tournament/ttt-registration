package com.tt.tournament.xmlimport.infrastructure.api

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.tt.tournament.infrastructure.db.H2TestDatabase
import com.tt.tournament.xmlimport.infrastructure.database.PlayerRepository
import org.assertj.core.api.Assertions
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
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
            WireMock.get(WireMock.urlEqualTo("/saturday-export"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml; charset=utf-8")
                        .withBody(saturdayXml)
                )
        )

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/sunday-export"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml; charset=utf-8")
                        .withBody(sundayXml)
                )
        )

        // when - we trigger the automatic import
        mockMvc.perform(
            MockMvcRequestBuilders.post("/import/automatic-tournament-import")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").password("password"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Both Saturday and Sunday tournaments imported successfully"))

        // verify both WireMock endpoints were called
        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/saturday-export")))
        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/sunday-export")))

        // verify data was imported (players should exist in database)
        val allPlayers = playerRepository.readAllPlayers()
        Assertions.assertThat(allPlayers).isNotEmpty()
    }

    @Test
    fun `Given saturday endpoint fails when automatic import triggered then returns partial success`() {
        // given - Saturday endpoint fails, Sunday succeeds
        val sundayXml = loadTestXmlFile("tournamentExport_Sonntag_2026.xml")

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/saturday-export"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")
                )
        )

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/sunday-export"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody(sundayXml)
                )
        )

        // when - we trigger the automatic import
        mockMvc.perform(
            MockMvcRequestBuilders.post("/import/automatic-tournament-import")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").password("password"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isPartialContent)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty)
    }

    @Test
    fun `Given sunday endpoint fails when automatic import triggered then returns partial success`() {
        // given - Saturday endpoint succeeds, Sunday fails
        val saturdayXml = loadTestXmlFile("tournamentExport_samstag.xml")

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/saturday-export"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody(saturdayXml)
                )
        )

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/sunday-export"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")
                )
        )

        // when - we trigger the automatic import
        mockMvc.perform(
            MockMvcRequestBuilders.post("/import/automatic-tournament-import")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").password("password"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isPartialContent)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]").value(Matchers.containsString("Sunday")))
    }

    @Test
    fun `Given both endpoints fail when automatic import triggered then returns error response`() {
        // given - Both endpoints fail
        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/saturday-export"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(404)
                        .withBody("Not Found")
                )
        )

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/sunday-export"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(404)
                        .withBody("Not Found")
                )
        )

        // when - we trigger the automatic import
        mockMvc.perform(
            MockMvcRequestBuilders.post("/import/automatic-tournament-import")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").password("password"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isPartialContent)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty)
    }

    @Test
    fun `Given invalid XML content when automatic import triggered then returns error response`() {
        // given - WireMock serves invalid XML
        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/saturday-export"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<invalid>xml without tournament root</invalid>")
                )
        )

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/sunday-export"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<invalid>xml without tournament root</invalid>")
                )
        )

        // when - we trigger the automatic import
        mockMvc.perform(
            MockMvcRequestBuilders.post("/import/automatic-tournament-import")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").password("password"))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isPartialContent)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
    }

    @Test
    fun `Given unauthenticated user when automatic import triggered then returns forbidden`() {
        // given - WireMock is set up but user is not authenticated
        val saturdayXml = loadTestXmlFile("tournamentExport_samstag.xml")
        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/saturday-export"))
                .willReturn(WireMock.aResponse().withStatus(200).withBody(saturdayXml))
        )

        // when - unauthenticated user tries to trigger import
        mockMvc.perform(
            MockMvcRequestBuilders.post("/import/automatic-tournament-import")
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    private fun loadTestXmlFile(filename: String): String {
        val resourcePath = Paths.get("src/test/resources/$filename")
        return Files.readString(resourcePath)
    }
}