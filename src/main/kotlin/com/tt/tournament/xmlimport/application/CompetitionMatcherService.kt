package com.tt.tournament.xmlimport.application

import com.tt.tournament.xmlimport.infrastructure.database.TypeEntity
import com.tt.tournament.xmlimport.infrastructure.database.TypeRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CompetitionMatcherService(private val typeRepository: TypeRepository) {

    private val logger = LoggerFactory.getLogger(CompetitionMatcherService::class.java)

    /**
     * Build competition name from XML data following tournament naming convention.
     * Example: "Jugend 19 B Einzel" or "Senioren 40 Einzel"
     */
    fun buildCompetitionName(competition: CompetitionDto): String {
        val ageGroup = competition.ageGroup.trim()
        val type = competition.type.trim()
        val ttrRemarks = competition.ttrRemarks?.trim() ?: ""

        return if (ttrRemarks.isNotBlank()) {
            "$ageGroup $ttrRemarks $type"
        } else {
            "$ageGroup $type"
        }
    }

    /**
     * Match competition to existing type by name, or create new type if not found.
     * Returns TypeEntity (either found or newly created).
     */
    fun matchOrCreateCompetitionType(competition: CompetitionDto): TypeEntity {
        val competitionName = buildCompetitionName(competition)
        val existingType = typeRepository.findByName(competitionName)

        if (existingType != null) {
            logger.debug("Matched competition '$competitionName' to existing Type_ID: ${existingType.id}")
            return existingType
        }

        // Create new type if no match found
        logger.info("No matching type found for competition: $competitionName - creating new type")
        typeRepository.create(competitionName, startGebuehr = 0.0)

        // Fetch the newly created type
        val newType = typeRepository.findByName(competitionName)
            ?: throw IllegalStateException("Failed to create or retrieve type: $competitionName")

        logger.info("Created new Type_ID: ${newType.id} for competition: $competitionName")
        return newType
    }

}
