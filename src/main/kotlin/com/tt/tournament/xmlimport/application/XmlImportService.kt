package com.tt.tournament.xmlimport.application

import com.tt.tournament.xmlimport.infrastructure.database.ClubRepository
import com.tt.tournament.xmlimport.infrastructure.database.PlayerRepository
import com.tt.tournament.xmlimport.infrastructure.database.TypeRepository
import com.tt.tournament.xmlimport.infrastructure.api.ImportResponse
import com.tt.tournament.xmlimport.infrastructure.api.ImportSummary
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class XmlImportService(
    private val xmlParserService: XmlParserService,
    private val competitionMatcherService: CompetitionMatcherService,
    private val playerRepository: PlayerRepository,
    private val clubRepository: ClubRepository,
    private val typeRepository: TypeRepository
) {

    private val logger = LoggerFactory.getLogger(XmlImportService::class.java)

    @Transactional
    fun importTournamentData(xmlContent: String): ImportResponse {
        try {
            logger.info("Starting tournament XML import")

            // Parse XML
            val tournament = xmlParserService.parseTournamentXml(xmlContent)
            logger.info("Parsed tournament: ${tournament.name}")

            // Initialize counters
            var playersImported = 0
            var playersUpdated = 0
            var clubsCreated = 0
            var competitionsMatched = 0
            var competitionsCreated = 0
            var enrollmentsCreated = 0
            var duplicatesSkipped = 0
            val errors = mutableListOf<String>()

            // Track existing type IDs to distinguish matched vs created
            val existingTypeIds = typeRepository.findAll().map { it.id }.toSet()

            // Process each competition
            for (competition in tournament.competitions) {
                val competitionName = competitionMatcherService.buildCompetitionName(competition)
                logger.debug("Processing competition: $competitionName")

                // Match or create competition type
                val typeEntity = competitionMatcherService.matchOrCreateCompetitionType(competition)

                // Track whether this was a match or a creation
                if (existingTypeIds.contains(typeEntity.id)) {
                    competitionsMatched++
                } else {
                    competitionsCreated++
                }

                // Process players in this competition
                val players = competition.players?.players ?: emptyList()
                for (playerXml in players) {
                    val person = playerXml.person ?: continue

                    try {
                        // Process club first
                        val clubId = processClub(person)
                        if (clubId == null) {
                            clubsCreated++
                            // Retry to get the newly created club ID
                            val newClubId = clubRepository.findByName(person.clubName)?.id
                            if (newClubId == null) {
                                errors.add("Failed to create club: ${person.clubName}")
                                continue
                            }

                            // Process player with new club
                            val playerResult = processPlayer(person, newClubId)
                            when (playerResult) {
                                is PlayerProcessResult.Created -> playersImported++
                                is PlayerProcessResult.Updated -> playersUpdated++
                            }

                            // Enroll player in type
                            val enrolled = enrollPlayerInCompetition(playerResult.playerId, typeEntity.id)
                            if (enrolled) {
                                enrollmentsCreated++
                            } else {
                                duplicatesSkipped++
                            }
                        } else {
                            // Club already exists
                            val playerResult = processPlayer(person, clubId)
                            when (playerResult) {
                                is PlayerProcessResult.Created -> playersImported++
                                is PlayerProcessResult.Updated -> playersUpdated++
                            }

                            // Enroll player in type
                            val enrolled = enrollPlayerInCompetition(playerResult.playerId, typeEntity.id)
                            if (enrolled) {
                                enrollmentsCreated++
                            } else {
                                duplicatesSkipped++
                            }
                        }

                    } catch (e: Exception) {
                        val errorMsg = "Failed to process player ${person.firstname} ${person.lastname} (${person.licenceNr}): ${e.message}"
                        logger.error(errorMsg, e)
                        errors.add(errorMsg)
                    }
                }
            }

            val summary = ImportSummary(
                playersImported = playersImported,
                playersUpdated = playersUpdated,
                clubsCreated = clubsCreated,
                competitionsMatched = competitionsMatched,
                competitionsCreated = competitionsCreated,
                enrollmentsCreated = enrollmentsCreated,
                duplicatesSkipped = duplicatesSkipped
            )

            logger.info("Import completed: $summary")

            return ImportResponse(
                success = errors.isEmpty(),
                message = if (errors.isEmpty()) "Import completed successfully" else "Import completed with errors",
                summary = summary,
                errors = errors
            )

        } catch (e: XmlParseException) {
            logger.error("XML parsing failed", e)
            return ImportResponse(
                success = false,
                message = "Failed to parse XML: ${e.message}",
                errors = listOf(e.message ?: "Unknown XML parsing error")
            )
        } catch (e: Exception) {
            logger.error("Import failed with unexpected error", e)
            return ImportResponse(
                success = false,
                message = "Import failed: ${e.message}",
                errors = listOf(e.message ?: "Unknown error")
            )
        }
    }

    private fun processClub(person: PersonDto): Int? {
        val existingClub = clubRepository.findByName(person.clubName)
        return if (existingClub != null) {
            logger.debug("Found existing club: ${person.clubName} (ID: ${existingClub.id})")
            existingClub.id
        } else {
            logger.info("Creating new club: ${person.clubName}")
            clubRepository.create(
                clubName = person.clubName,
                verband = person.clubFederationNickname,
                clubNr = person.clubNr
            )
            null  // Return null to indicate club was created (caller needs to refetch)
        }
    }

    private fun processPlayer(person: PersonDto, clubId: Int): PlayerProcessResult {
        val existingPlayer = playerRepository.findByLicenseNr(person.licenceNr)

        return if (existingPlayer != null) {
            logger.debug("Updating existing player: ${person.firstname} ${person.lastname} (${person.licenceNr})")
            playerRepository.updatePlayer(
                playerId = existingPlayer.id,
                firstName = person.firstname,
                lastName = person.lastname,
                clubId = clubId,
                sex = convertSex(person.sex),
                nationality = person.nationality,
                ttr = person.ttr?.toDoubleOrNull(),
                birthYear = person.birthyear
            )
            PlayerProcessResult.Updated(existingPlayer.id)
        } else {
            logger.info("Creating new player: ${person.firstname} ${person.lastname} (${person.licenceNr})")
            val newPlayerId = playerRepository.createPlayer(
                firstName = person.firstname,
                lastName = person.lastname,
                licenseNr = person.licenceNr,
                clubId = clubId,
                sex = convertSex(person.sex),
                nationality = person.nationality,
                ttr = person.ttr?.toDoubleOrNull(),
                birthYear = person.birthyear
            )
            PlayerProcessResult.Created(newPlayerId)
        }
    }

    private fun enrollPlayerInCompetition(playerId: Int, typeId: Int): Boolean {
        if (playerRepository.isPlayerEnrolledInType(playerId, typeId)) {
            logger.debug("Player $playerId already enrolled in type $typeId, skipping")
            return false
        }

        playerRepository.enrollPlayerInType(playerId, typeId, paid = 0)
        logger.debug("Enrolled player $playerId in type $typeId")
        return true
    }

    private fun convertSex(sexCode: String): String? {
        return when (sexCode) {
            "0" -> "w"  // weiblich
            "1" -> "m"  // männlich
            else -> null
        }
    }
}

sealed class PlayerProcessResult {
    abstract val playerId: Int
    data class Created(override val playerId: Int) : PlayerProcessResult()
    data class Updated(override val playerId: Int) : PlayerProcessResult()
}
