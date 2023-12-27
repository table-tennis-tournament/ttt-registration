package com.tt.tournament.infrastructure.db

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
                    "                and t.Type_ID > 20" +
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
                    "                and t.Type_ID < 20" +
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

    fun readPlayersForDiscipline() : Map<String, List<Player>> {
        val sql = "SELECT P.Play_FirstName, P.Play_LastName,  t.Type_Name, tp.typl_paid, c.Club_Name, c.Club_AdresseOrt, t.Type_Name, t.Type_ID, t.Type_StartGebuehr, P.Play_ID, tp.typl_paid" +
                    "                FROM typeperplayer tp, player P, type t, club c " +
                    "                where tp.typl_play_id = P.Play_ID AND t.Type_ID = tp.typl_type_id" +
                    "                and c.Club_ID = P.Play_Club_ID" +
                    "                order by P.Play_Club_ID, P.Play_LastName"

        val result = jdbcClient.sql(sql)
            .query(this::mapToPlayer)
        val resultMap = HashMap<String, List<Player>>()
        for (player in result) {
            if(resultMap.contains(player.disciplines[0].name)) {
                resultMap[player.disciplines[0].name]?.addLast(player)
                continue
            }
            resultMap[player.disciplines[0].name] = mutableListOf(player)
        }
        return resultMap
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