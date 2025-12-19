package com.tt.tournament.infrastructure.xml

import com.tt.tournament.infrastructure.config.ImportProperties
import com.tt.tournament.infrastructure.rest.ImportResponse
import com.tt.tournament.infrastructure.rest.ImportSummary
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.web.client.body
import java.net.URI

@Service
class AutomaticImportService(
    private val importProperties: ImportProperties,
    private val xmlImportService: XmlImportService,
    private val restClient: RestClient
) {

    private val logger = LoggerFactory.getLogger(AutomaticImportService::class.java)

    fun importTournamentDataFromConfiguredUrls(): ImportResponse {
        logger.info("Starting automatic tournament import from configured URLs")

        val saturdayResult = downloadAndImport(importProperties.saturday, "Saturday")
        val sundayResult = downloadAndImport(importProperties.sunday, "Sunday")

        return combineResults(saturdayResult, sundayResult)
    }

    fun importSaturdayTournament(): ImportResponse {
        logger.info("Starting automatic Saturday tournament import")

        val saturdayResult = downloadAndImport(importProperties.saturday, "Saturday")

        return when (saturdayResult) {
            is ImportResult.Success -> ImportResponse(
                success = true,
                message = "Saturday tournament imported successfully",
                summary = saturdayResult.summary,
                errors = emptyList()
            )
            is ImportResult.PartialSuccess -> ImportResponse(
                success = false,
                message = "Saturday tournament import completed with errors",
                summary = saturdayResult.summary,
                errors = saturdayResult.errors
            )
            is ImportResult.Failure -> ImportResponse(
                success = false,
                message = "Failed to import Saturday tournament",
                summary = null,
                errors = listOf(saturdayResult.errorMessage)
            )
        }
    }

    fun importSundayTournament(): ImportResponse {
        logger.info("Starting automatic Sunday tournament import")

        val sundayResult = downloadAndImport(importProperties.sunday, "Sunday")

        return when (sundayResult) {
            is ImportResult.Success -> ImportResponse(
                success = true,
                message = "Sunday tournament imported successfully",
                summary = sundayResult.summary,
                errors = emptyList()
            )
            is ImportResult.PartialSuccess -> ImportResponse(
                success = false,
                message = "Sunday tournament import completed with errors",
                summary = sundayResult.summary,
                errors = sundayResult.errors
            )
            is ImportResult.Failure -> ImportResponse(
                success = false,
                message = "Failed to import Sunday tournament",
                summary = null,
                errors = listOf(sundayResult.errorMessage)
            )
        }
    }

    private fun downloadAndImport(url: String, name: String): ImportResult {
        return try {
            logger.info("Downloading $name tournament data from: $url")

            val xmlContent = restClient.get()
                .uri(URI.create(url))
                .retrieve()
                .body<String>()

            if (xmlContent.isNullOrBlank()) {
                logger.error("$name: Downloaded XML content is empty")
                return ImportResult.Failure(name, "Downloaded XML content is empty")
            }

            logger.info("$name: Successfully downloaded XML (${xmlContent.length} characters)")

            val response = xmlImportService.importTournamentData(xmlContent)

            if (response.success) {
                logger.info("$name: Import successful - ${response.summary}")
                ImportResult.Success(name, response.summary!!)
            } else {
                logger.warn("$name: Import completed with errors - ${response.errors}")
                ImportResult.PartialSuccess(name, response.summary, response.errors)
            }

        } catch (e: RestClientException) {
            logger.error("$name: Failed to download XML from URL: $url", e)
            ImportResult.Failure(name, "Failed to download XML: ${e.message}")
        } catch (e: Exception) {
            logger.error("$name: Unexpected error during import", e)
            ImportResult.Failure(name, "Unexpected error: ${e.message}")
        }
    }

    private fun combineResults(saturdayResult: ImportResult, sundayResult: ImportResult): ImportResponse {
        val combinedSummary = combineSummaries(saturdayResult, sundayResult)
        val combinedErrors = combineErrors(saturdayResult, sundayResult)

        val success = saturdayResult is ImportResult.Success && sundayResult is ImportResult.Success

        val message = when {
            success -> "Both Saturday and Sunday tournaments imported successfully"
            combinedErrors.isEmpty() -> "Tournament import completed"
            saturdayResult is ImportResult.Failure && sundayResult is ImportResult.Failure ->
                "Failed to import both tournaments"
            else -> "Tournament import completed with errors"
        }

        return ImportResponse(
            success = success,
            message = message,
            summary = combinedSummary,
            errors = combinedErrors
        )
    }

    private fun combineSummaries(saturdayResult: ImportResult, sundayResult: ImportResult): ImportSummary {
        val saturdaySummary = saturdayResult.summary() ?: ImportSummary(0, 0, 0, 0, 0, 0, 0)
        val sundaySummary = sundayResult.summary() ?: ImportSummary(0, 0, 0, 0, 0, 0, 0)

        return ImportSummary(
            playersImported = saturdaySummary.playersImported + sundaySummary.playersImported,
            playersUpdated = saturdaySummary.playersUpdated + sundaySummary.playersUpdated,
            clubsCreated = saturdaySummary.clubsCreated + sundaySummary.clubsCreated,
            competitionsMatched = saturdaySummary.competitionsMatched + sundaySummary.competitionsMatched,
            competitionsCreated = saturdaySummary.competitionsCreated + sundaySummary.competitionsCreated,
            enrollmentsCreated = saturdaySummary.enrollmentsCreated + sundaySummary.enrollmentsCreated,
            duplicatesSkipped = saturdaySummary.duplicatesSkipped + sundaySummary.duplicatesSkipped
        )
    }

    private fun combineErrors(saturdayResult: ImportResult, sundayResult: ImportResult): List<String> {
        val errors = mutableListOf<String>()

        when (saturdayResult) {
            is ImportResult.Failure -> errors.add("Saturday: ${saturdayResult.errorMessage}")
            is ImportResult.PartialSuccess -> errors.addAll(saturdayResult.errors.map { "Saturday: $it" })
            is ImportResult.Success -> { /* no errors */ }
        }

        when (sundayResult) {
            is ImportResult.Failure -> errors.add("Sunday: ${sundayResult.errorMessage}")
            is ImportResult.PartialSuccess -> errors.addAll(sundayResult.errors.map { "Sunday: $it" })
            is ImportResult.Success -> { /* no errors */ }
        }

        return errors
    }
}

sealed class ImportResult {
    abstract val name: String

    data class Success(override val name: String, val summary: ImportSummary) : ImportResult()
    data class PartialSuccess(override val name: String, val summary: ImportSummary?, val errors: List<String>) : ImportResult()
    data class Failure(override val name: String, val errorMessage: String) : ImportResult()

    fun summary(): ImportSummary? = when (this) {
        is Success -> summary
        is PartialSuccess -> summary
        is Failure -> null
    }
}