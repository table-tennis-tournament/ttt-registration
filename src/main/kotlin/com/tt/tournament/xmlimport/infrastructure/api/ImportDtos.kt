package com.tt.tournament.xmlimport.infrastructure.api

data class ImportResponse(
    val success: Boolean,
    val message: String,
    val summary: ImportSummary? = null,
    val errors: List<String> = emptyList()
)

data class ImportSummary(
    val playersImported: Int,       // New players created
    val playersUpdated: Int,        // Existing players updated
    val clubsCreated: Int,          // New clubs created
    val competitionsMatched: Int,   // Competitions matched to existing types
    val competitionsCreated: Int,   // New types created for competitions
    val enrollmentsCreated: Int,    // typeperplayer records created
    val duplicatesSkipped: Int,     // Players already enrolled in type
    val enrollmentsDeleted: Int = 0 // typeperplayer records deleted (players not in XML)
)
