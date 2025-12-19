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
import org.junit.jupiter.api.assertNotNull
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
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths

private const val SUNDAY_URL = "/sunday-export"
private const val SATURDAY_URL = "/saturday-export"

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(H2TestDatabase::class)
@TestPropertySource(properties = [
    "application.import.saturday=http://localhost:8090$SATURDAY_URL",
    "application.import.sunday=http://localhost:8090$SUNDAY_URL"
])
class SeparateImportEndpointsIT(
    @param:Autowired val mockMvc: MockMvc,
    @param:Autowired val playerRepository: PlayerRepository
) {

    private lateinit var wireMockServer: WireMockServer

    @BeforeEach
    fun setup() {
        wireMockServer = WireMockServer(WireMockConfiguration.options().port(8090))
        wireMockServer.start()
    }

    @AfterEach
    fun teardown() {
        wireMockServer.stop()
    }

    @Test
    fun `Given wiremock serves Saturday XML when Saturday import triggered then file is imported successfully`() {
        // given - WireMock serves Saturday tournament XML file
        val saturdayXml = loadTestXmlFile("tournamentExport_samstag.xml")

        wireMockServer.stubFor(
            get(urlEqualTo(SATURDAY_URL))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml; charset=utf-8")
                        .withBody(saturdayXml)
                )
        )

        // when - we trigger the Saturday import
        mockMvc.perform(
            post("/import/automatic-saturday-import")
                .with(user("admin").password("password"))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Saturday tournament imported successfully"))
            .andExpect(jsonPath("$.summary").exists())
            .andExpect(jsonPath("$.summary.playersImported").exists())

        // verify WireMock endpoint was called
        wireMockServer.verify(getRequestedFor(urlEqualTo(SATURDAY_URL)))

        // verify data was imported (players should exist in database)
        val allPlayers = playerRepository.readAllPlayers()
        assertThat(allPlayers).isNotEmpty()
    }

    @Test
    fun `Given wiremock serves Sunday XML when Sunday import triggered then file is imported successfully`() {
        // given - WireMock serves Sunday tournament XML file
        val sundayXml = loadTestXmlFile("tournamentExport_Sonntag_2026.xml")

        wireMockServer.stubFor(
            get(urlEqualTo(SUNDAY_URL))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml; charset=utf-8")
                        .withBody(sundayXml)
                )
        )

        // when - we trigger the Sunday import
        mockMvc.perform(
            post("/import/automatic-sunday-import")
                .with(user("admin").password("password"))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Sunday tournament imported successfully"))
            .andExpect(jsonPath("$.summary").exists())
            .andExpect(jsonPath("$.summary.playersImported").exists())

        // verify WireMock endpoint was called
        wireMockServer.verify(getRequestedFor(urlEqualTo(SUNDAY_URL)))

        // verify data was imported (players should exist in database)
        val allPlayers = playerRepository.readAllPlayers()
        assertThat(allPlayers).isNotEmpty()
    }

    @Test
    fun `Given saturday endpoint fails when Saturday import triggered then returns error response`() {
        // given - Saturday endpoint fails
        wireMockServer.stubFor(
            get(urlEqualTo(SATURDAY_URL))
                .willReturn(
                    aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")
                )
        )

        // when - we trigger the Saturday import
        mockMvc.perform(
            post("/import/automatic-saturday-import")
                .with(user("admin").password("password"))
                .with(csrf())
        )
            .andExpect(status().isPartialContent)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Failed to import Saturday tournament"))
            .andExpect(jsonPath("$.errors").isNotEmpty)
            .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.containsString("Failed to download XML")))
    }

    @Test
    fun `Given sunday endpoint fails when Sunday import triggered then returns error response`() {
        // given - Sunday endpoint fails
        wireMockServer.stubFor(
            get(urlEqualTo(SUNDAY_URL))
                .willReturn(
                    aResponse()
                        .withStatus(404)
                        .withBody("Not Found")
                )
        )

        // when - we trigger the Sunday import
        mockMvc.perform(
            post("/import/automatic-sunday-import")
                .with(user("admin").password("password"))
                .with(csrf())
        )
            .andExpect(status().isPartialContent)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Failed to import Sunday tournament"))
            .andExpect(jsonPath("$.errors").isNotEmpty)
    }

    @Test
    fun `Given invalid XML content when Saturday import triggered then returns error response`() {
        // given - WireMock serves invalid XML
        wireMockServer.stubFor(
            get(urlEqualTo(SATURDAY_URL))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<invalid>xml without tournament root</invalid>")
                )
        )

        // when - we trigger the Saturday import
        mockMvc.perform(
            post("/import/automatic-saturday-import")
                .with(user("admin").password("password"))
                .with(csrf())
        )
            .andExpect(status().isPartialContent)
            .andExpect(jsonPath("$.success").value(false))
    }

    @Test
    fun `Given invalid XML content when Sunday import triggered then returns error response`() {
        // given - WireMock serves invalid XML
        wireMockServer.stubFor(
            get(urlEqualTo(SUNDAY_URL))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<invalid>xml without tournament root</invalid>")
                )
        )

        // when - we trigger the Sunday import
        mockMvc.perform(
            post("/import/automatic-sunday-import")
                .with(user("admin").password("password"))
                .with(csrf())
        )
            .andExpect(status().isPartialContent)
            .andExpect(jsonPath("$.success").value(false))
    }

    @Test
    fun `Given unauthenticated user when Saturday import triggered then returns forbidden`() {
        // given - WireMock is set up but user is not authenticated
        val saturdayXml = loadTestXmlFile("tournamentExport_samstag.xml")
        wireMockServer.stubFor(
            get(urlEqualTo(SATURDAY_URL))
                .willReturn(aResponse().withStatus(200).withBody(saturdayXml))
        )

        // when - unauthenticated user tries to trigger import
        mockMvc.perform(
            post("/import/automatic-saturday-import")
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `Given unauthenticated user when Sunday import triggered then returns forbidden`() {
        // given - WireMock is set up but user is not authenticated
        val sundayXml = loadTestXmlFile("tournamentExport_Sonntag_2026.xml")
        wireMockServer.stubFor(
            get(urlEqualTo(SUNDAY_URL))
                .willReturn(aResponse().withStatus(200).withBody(sundayXml))
        )

        // when - unauthenticated user tries to trigger import
        mockMvc.perform(
            post("/import/automatic-sunday-import")
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `Given both imports run separately when both succeed then both tournaments are imported`() {
        // given - WireMock serves both tournament XML files
        val saturdayXml = loadTestXmlFile("tournamentExport_samstag.xml")
        val sundayXml = loadTestXmlFile("tournamentExport_Sonntag_2026.xml")

        wireMockServer.stubFor(
            get(urlEqualTo(SATURDAY_URL))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml; charset=utf-8")
                        .withBody(saturdayXml)
                )
        )

        wireMockServer.stubFor(
            get(urlEqualTo(SUNDAY_URL))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml; charset=utf-8")
                        .withBody(sundayXml)
                )
        )

        // when - we trigger Saturday import first
        mockMvc.perform(
            post("/import/automatic-saturday-import")
                .with(user("admin").password("password"))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Saturday tournament imported successfully"))

        // then trigger Sunday import
        mockMvc.perform(
            post("/import/automatic-sunday-import")
                .with(user("admin").password("password"))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Sunday tournament imported successfully"))

        // verify both WireMock endpoints were called
        wireMockServer.verify(getRequestedFor(urlEqualTo(SATURDAY_URL)))
        wireMockServer.verify(getRequestedFor(urlEqualTo(SUNDAY_URL)))

        // verify data was imported (players should exist in database)
        val allPlayers = playerRepository.readAllPlayers()
        assertThat(allPlayers).isNotEmpty()
    }

    @Test
    fun `Given empty XML content when Saturday import triggered then returns error response`() {
        // given - WireMock serves empty content
        wireMockServer.stubFor(
            get(urlEqualTo(SATURDAY_URL))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody("")
                )
        )

        // when - we trigger the Saturday import
        mockMvc.perform(
            post("/import/automatic-saturday-import")
                .with(user("admin").password("password"))
                .with(csrf())
        )
            .andExpect(status().isPartialContent)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.containsString("empty")))
    }

    @Test
    fun `Given empty XML content when Sunday import triggered then returns error response`() {
        // given - WireMock serves empty content
        wireMockServer.stubFor(
            get(urlEqualTo(SUNDAY_URL))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody("")
                )
        )

        // when - we trigger the Sunday import
        mockMvc.perform(
            post("/import/automatic-sunday-import")
                .with(user("admin").password("password"))
                .with(csrf())
        )
            .andExpect(status().isPartialContent)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.containsString("empty")))
    }

    @Test
    fun `test directly` () {
        val restClient = RestClient.create()
        val xmlContent = restClient.get()
            .uri(URI.create("https://www.click-tt.de/cgi-bin/WebObjects/nuLigaKurierTTDE.woa/wa/tournamentExport?tournamentId=wxbpSFHJMQ9fxCdl%2B2dLH5tClATFLQ71"))
            .retrieve()
            .body<String>()

        assertNotNull(xmlContent)
        assertThat(xmlContent).contains("<tournament")
    }

    private fun loadTestXmlFile(filename: String): String {
        val resourcePath = Paths.get("src/test/resources/$filename")
        return Files.readString(resourcePath)
    }
}