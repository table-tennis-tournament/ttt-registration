package com.tt.tournament.xmlimport.infrastructure.database

import com.tt.tournament.accounting.domain.Discipline
import com.tt.tournament.accounting.domain.Player
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class PlayerRepository(val jdbcClient: JdbcClient) {

    fun readAllPlayersForSunday() : List<Player> {
        val sql = "SELECT P.Play_FirstName, P.Play_LastName,  t.Type_Name, tp.typl_paid, c.Club_Name, c.Club_AdresseOrt, t.Type_Name, t.Type_ID, t.Type_StartGebuehr, P.Play_ID, tp.typl_paid" +
                    "                FROM typeperplayer tp, player P, type t, club c " +
                    "                where tp.typl_play_id = P.Play_ID AND t.Type_ID = tp.typl_type_id" +
                    "                and c.Club_ID = P.Play_Club_ID" +
                    "                and t.Type_ID > 30" +
                    "                and t.Type_Kind = 1" +
                    "                order by P.Play_Club_ID, P.Play_LastName"

        val result = jdbcClient.sql(sql)
            .query(this::mapToPlayer)
        val resultMap = HashMap<Number, Player>()
        for (player in result) {
            if(resultMap.contains(player.id)) {
                val existingEntry = resultMap[player.id]
                existingEntry?.addDisciplines(player.disciplines)
                continue
            }
            resultMap[player.id] = player
        }
        return resultMap.values.toList()


    }

    fun readAllPlayersForSaturday() : List<Player> {
        val sql = "SELECT P.Play_FirstName, P.Play_LastName,  t.Type_Name, tp.typl_paid, c.Club_Name, c.Club_AdresseOrt, t.Type_Name, t.Type_ID, t.Type_StartGebuehr, P.Play_ID, tp.typl_paid" +
                    "                FROM typeperplayer tp, player P, type t, club c " +
                    "                where tp.typl_play_id = P.Play_ID AND t.Type_ID = tp.typl_type_id" +
                    "                and c.Club_ID = P.Play_Club_ID" +
                    "                and t.Type_ID < 31" +
                    "                and t.Type_Kind = 1" +
                    "                order by P.Play_Club_ID, P.Play_LastName"

        val result = jdbcClient.sql(sql)
            .query(this::mapToPlayer)
        val resultMap = HashMap<Number, Player>()
        for (player in result) {
            if(resultMap.contains(player.id)) {
                val existingEntry = resultMap[player.id]
                existingEntry?.addDisciplines(player.disciplines)
                continue
            }
            resultMap[player.id] = player
        }
        return resultMap.values.toList()
    }

    fun readPlayersForDiscipline() : Map<String, MutableList<Player>> {
        val sql = "SELECT P.Play_FirstName, P.Play_LastName,  t.Type_Name, tp.typl_paid, c.Club_Name, c.Club_AdresseOrt, t.Type_Name, t.Type_ID, t.Type_StartGebuehr, P.Play_ID, tp.typl_paid" +
                    "                FROM typeperplayer tp, player P, type t, club c " +
                    "                where tp.typl_play_id = P.Play_ID AND t.Type_ID = tp.typl_type_id" +
                    "                and c.Club_ID = P.Play_Club_ID" +
                    "                and t.Type_Kind = 1" +
                    "                order by P.Play_Club_ID, P.Play_LastName"

        val result = jdbcClient.sql(sql)
            .query(this::mapToPlayer)
        val resultMap = HashMap<String, MutableList<Player>>()
        for (player in result) {
            if(resultMap.contains(player.disciplines[0].name)) {
                resultMap[player.disciplines[0].name]?.add(player)
                continue
            }
            resultMap[player.disciplines[0].name] = mutableListOf(player)
        }
        return resultMap
    }

    fun readAllPlayers() : List<Player> {
        val sql = "SELECT P.Play_FirstName, P.Play_LastName,  t.Type_Name, tp.typl_paid, c.Club_Name, c.Club_AdresseOrt, t.Type_Name, t.Type_ID, t.Type_StartGebuehr, P.Play_ID, tp.typl_paid" +
                    "                FROM typeperplayer tp, player P, type t, club c " +
                    "                where tp.typl_play_id = P.Play_ID AND t.Type_ID = tp.typl_type_id" +
                    "                and c.Club_ID = P.Play_Club_ID" +
                    "                order by P.Play_LastName, P.Play_FirstName"

        val result = jdbcClient.sql(sql)
            .query(this::mapToPlayer)
        val resultMap = HashMap<Number, Player>()
        for (player in result) {
            if(resultMap.contains(player.id)) {
                val existingEntry = resultMap[player.id]
                existingEntry?.addDisciplines(player.disciplines)
                continue
            }
            resultMap[player.id] = player
        }
        return resultMap.values.toList()
    }



    fun updatePaymentStatus(playerId: Number, paid: Boolean): Int {
        val paidValue = if (paid) 1 else 0
        val sql = "UPDATE typeperplayer SET typl_paid = :paid WHERE typl_play_id = :playerId"

        return jdbcClient.sql(sql)
            .param("paid", paidValue)
            .param("playerId", playerId)
            .update()
    }

    // XML Import Extension Methods

    fun findByLicenseNr(licenseNr: String): PlayerEntity? {
        val sql = """
            SELECT Play_ID, Play_FirstName, Play_LastName, Play_LicenseNr,
                   Play_Club_ID, Play_Sex, Play_Nationality, Play_TTR
            FROM player
            WHERE Play_LicenseNr = :licenseNr
        """
        return jdbcClient.sql(sql)
            .param("licenseNr", licenseNr)
            .query { rs, _ -> mapToPlayerEntity(rs) }
            .optional()
            .orElse(null)
    }

    fun createPlayer(
        firstName: String,
        lastName: String,
        licenseNr: String,
        clubId: Int,
        sex: String?,
        nationality: String?,
        ttr: Double?,
        birthYear: String?
    ): Int {
        val sql = """
            INSERT INTO player
            (Play_FirstName, Play_LastName, Play_LicenseNr, Play_Club_ID,
             Play_Sex, Play_Nationality, Play_TTR, Play_BirthDate)
            VALUES (:firstName, :lastName, :licenseNr, :clubId,
                    :sex, :nationality, :ttr, :birthDate)
        """

        val birthDate = birthYear?.let { "$it-01-01 00:00:00" }

        jdbcClient.sql(sql)
            .param("firstName", firstName)
            .param("lastName", lastName)
            .param("licenseNr", licenseNr)
            .param("clubId", clubId)
            .param("sex", sex)
            .param("nationality", nationality)
            .param("ttr", ttr)
            .param("birthDate", birthDate)
            .update()

        val lastIdSql = "SELECT LAST_INSERT_ID() as id"
        return jdbcClient.sql(lastIdSql)
            .query { rs, _ -> rs.getInt("id") }
            .single()
    }

    fun updatePlayer(
        playerId: Int,
        firstName: String,
        lastName: String,
        clubId: Int,
        sex: String?,
        nationality: String?,
        ttr: Double?,
        birthYear: String?
    ): Int {
        val sql = """
            UPDATE player
            SET Play_FirstName = :firstName,
                Play_LastName = :lastName,
                Play_Club_ID = :clubId,
                Play_Sex = :sex,
                Play_Nationality = :nationality,
                Play_TTR = :ttr,
                Play_BirthDate = :birthDate
            WHERE Play_ID = :playerId
        """

        val birthDate = birthYear?.let { "$it-01-01 00:00:00" }

        return jdbcClient.sql(sql)
            .param("playerId", playerId)
            .param("firstName", firstName)
            .param("lastName", lastName)
            .param("clubId", clubId)
            .param("sex", sex)
            .param("nationality", nationality)
            .param("ttr", ttr)
            .param("birthDate", birthDate)
            .update()
    }

    fun isPlayerEnrolledInType(playerId: Int, typeId: Int): Boolean {
        val sql = """
            SELECT COUNT(*) as count
            FROM typeperplayer
            WHERE typl_play_id = :playerId
            AND typl_type_id = :typeId
        """
        val count = jdbcClient.sql(sql)
            .param("playerId", playerId)
            .param("typeId", typeId)
            .query { rs, _ -> rs.getInt("count") }
            .single()
        return count > 0
    }

    fun enrollPlayerInType(playerId: Int, typeId: Int, paid: Int = 0): Int {
        val sql = """
            INSERT INTO typeperplayer (typl_play_id, typl_type_id, typl_seed, typl_paid)
            VALUES (:playerId, :typeId, 0, :paid)
        """
        return jdbcClient.sql(sql)
            .param("playerId", playerId)
            .param("typeId", typeId)
            .param("paid", paid)
            .update()
    }

    private fun mapToPlayerEntity(rs: ResultSet): PlayerEntity {
        return PlayerEntity(
            id = rs.getInt("Play_ID"),
            firstName = rs.getString("Play_FirstName"),
            lastName = rs.getString("Play_LastName"),
            licenseNr = rs.getString("Play_LicenseNr"),
            clubId = rs.getInt("Play_Club_ID"),
            sex = rs.getString("Play_Sex"),
            nationality = rs.getString("Play_Nationality"),
            ttr = rs.getDouble("Play_TTR")
        )
    }

    fun mapToPlayer(resultSet: ResultSet) : List<Player> {
        val result = mutableListOf<Player>()
        while(resultSet.next()) {
            val id = resultSet.getInt("Play_ID")
            val firstName = resultSet.getString("Play_FirstName")
            val lastName = resultSet.getString("Play_LastName")
            val clubName = resultSet.getString("Club_Name")
            val clubCity = resultSet.getString("Club_AdresseOrt")
            val typeId = resultSet.getInt("Type_ID")
            val typeName = resultSet.getString("Type_Name")
            val price = resultSet.getInt("Type_StartGebuehr")
            val paid = resultSet.getInt("typl_paid")
            val discipline = Discipline(typeId, typeName, price, paid)
            result.add(Player(id, firstName, lastName, clubName, clubCity, mutableListOf(discipline)))
        }
        return result
    }
}

data class PlayerEntity(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val licenseNr: String?,
    val clubId: Int,
    val sex: String?,
    val nationality: String?,
    val ttr: Double?
)