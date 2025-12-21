package com.tt.tournament.xmlimport.infrastructure.database

import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository

@Repository
class ClubRepository(val jdbcClient: JdbcClient) {

    fun findByName(clubName: String): Club? {
        val sql = "SELECT Club_ID, Club_Name, Club_Verband, Club_Nr FROM club WHERE Club_Name = :clubName"
        return jdbcClient.sql(sql)
            .param("clubName", clubName)
            .query { rs, _ ->
                Club(
                    id = rs.getInt("Club_ID"),
                    name = rs.getString("Club_Name"),
                    verband = rs.getString("Club_Verband"),
                    clubNr = rs.getString("Club_Nr")
                )
            }
            .optional()
            .orElse(null)
    }

    fun create(clubName: String, verband: String, clubNr: String): Int {
        val sql = "INSERT INTO club (Club_Name, Club_Verband, Club_Nr) VALUES (:clubName, :verband, :clubNr)"
        jdbcClient.sql(sql)
            .param("clubName", clubName)
            .param("verband", verband)
            .param("clubNr", clubNr)
            .update()

        // Get last insert ID
        val lastIdSql = "SELECT LAST_INSERT_ID() as id"
        return jdbcClient.sql(lastIdSql)
            .query { rs, _ -> rs.getInt("id") }
            .single()
    }
}

data class Club(
    val id: Int,
    val name: String,
    val verband: String?,
    val clubNr: String?
)
