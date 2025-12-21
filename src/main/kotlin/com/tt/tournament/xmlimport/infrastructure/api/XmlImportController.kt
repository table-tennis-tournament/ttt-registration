package com.tt.tournament.xmlimport.infrastructure.api

import com.tt.tournament.xmlimport.application.AutomaticImportService
import com.tt.tournament.xmlimport.application.XmlImportService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.charset.StandardCharsets

@RestController
class XmlImportController(
    private val xmlImportService: XmlImportService,
    private val automaticImportService: AutomaticImportService
) {

    private val logger = LoggerFactory.getLogger(XmlImportController::class.java)
    private val maxFileSize = 10 * 1024 * 1024 // 10MB

    @PostMapping("/import/tournament-xml", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun importTournamentXml(@RequestParam("file") file: MultipartFile): ResponseEntity<ImportResponse> {
        return try {
            // Validate file
            if (file.isEmpty) {
                return ResponseEntity.badRequest().body(
                    ImportResponse(success = false, message = "File is empty")
                )
            }

            if (file.size > maxFileSize) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
                    ImportResponse(success = false, message = "File size exceeds 10MB limit")
                )
            }

            if (file.contentType != "text/xml" && file.contentType != "application/xml") {
                logger.warn("Unexpected content type: ${file.contentType}, proceeding anyway")
            }

            // Read XML content
            val xmlContent = String(file.bytes, StandardCharsets.UTF_8)

            // Validate XML structure (basic check)
            if (!xmlContent.contains("<tournament") || !xmlContent.contains("</tournament>")) {
                return ResponseEntity.badRequest().body(
                    ImportResponse(success = false, message = "Invalid XML: missing tournament root element")
                )
            }

            // Process import
            logger.info("Processing XML import from file: ${file.originalFilename}")
            val response = xmlImportService.importTournamentData(xmlContent)

            // Return appropriate HTTP status
            val httpStatus = when {
                response.success -> HttpStatus.OK
                response.errors.isNotEmpty() -> HttpStatus.PARTIAL_CONTENT
                else -> HttpStatus.BAD_REQUEST
            }

            ResponseEntity.status(httpStatus).body(response)

        } catch (e: Exception) {
            logger.error("Failed to process XML import", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ImportResponse(
                    success = false,
                    message = "Failed to process import: ${e.message}",
                    errors = listOf(e.message ?: "Unknown error")
                )
            )
        }
    }

    @PostMapping("/import/automatic-tournament-import")
    fun automaticTournamentImport(): ResponseEntity<ImportResponse> {
        return try {
            logger.info("Starting automatic tournament import from configured URLs")
            val response = automaticImportService.importTournamentDataFromConfiguredUrls()

            // Return appropriate HTTP status
            val httpStatus = when {
                response.success -> HttpStatus.OK
                response.errors.isNotEmpty() -> HttpStatus.PARTIAL_CONTENT
                else -> HttpStatus.BAD_REQUEST
            }

            ResponseEntity.status(httpStatus).body(response)

        } catch (e: Exception) {
            logger.error("Failed to process automatic tournament import", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ImportResponse(
                    success = false,
                    message = "Failed to process automatic import: ${e.message}",
                    errors = listOf(e.message ?: "Unknown error")
                )
            )
        }
    }

    @PostMapping("/import/automatic-saturday-import")
    fun automaticSaturdayImport(): ResponseEntity<ImportResponse> {
        return try {
            logger.info("Starting automatic Saturday tournament import")
            val response = automaticImportService.importSaturdayTournament()

            // Return appropriate HTTP status
            val httpStatus = when {
                response.success -> HttpStatus.OK
                response.errors.isNotEmpty() -> HttpStatus.PARTIAL_CONTENT
                else -> HttpStatus.BAD_REQUEST
            }

            ResponseEntity.status(httpStatus).body(response)

        } catch (e: Exception) {
            logger.error("Failed to process automatic Saturday tournament import", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ImportResponse(
                    success = false,
                    message = "Failed to process Saturday import: ${e.message}",
                    errors = listOf(e.message ?: "Unknown error")
                )
            )
        }
    }

    @PostMapping("/import/automatic-sunday-import")
    fun automaticSundayImport(): ResponseEntity<ImportResponse> {
        return try {
            logger.info("Starting automatic Sunday tournament import")
            val response = automaticImportService.importSundayTournament()

            // Return appropriate HTTP status
            val httpStatus = when {
                response.success -> HttpStatus.OK
                response.errors.isNotEmpty() -> HttpStatus.PARTIAL_CONTENT
                else -> HttpStatus.BAD_REQUEST
            }

            ResponseEntity.status(httpStatus).body(response)

        } catch (e: Exception) {
            logger.error("Failed to process automatic Sunday tournament import", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ImportResponse(
                    success = false,
                    message = "Failed to process Sunday import: ${e.message}",
                    errors = listOf(e.message ?: "Unknown error")
                )
            )
        }
    }
}