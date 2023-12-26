package com.tt.tournament.infrastructure.db

import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class PlayerRepository(val jdbcClient: JdbcClient) {

    fun readAllPlayersForSunday() : List<Player> {
        val sql =
            """
                SELECT P.Play_FirstName, P.Play_LastName,  t.Type_Name, tp.typl_paid, c.Club_Name, t.Type_Name, 
                t.Type_ID, t.Type_StartGebuehr, P.Play_ID, tp.typl_paid
                FROM `typeperplayer` tp, `player` P, `type` t, `club` c
                where tp.typl_play_id = P.Play_ID
                AND t.Type_ID = tp.typl_type_id
                and c.Club_ID = P.Play_Club_ID
                and t.Type_ID > 20
                order by P.Play_Club_ID, P.Play_LastName
            """
        return jdbcClient.sql(sql)
            .params("typeId", 20)
            .query(this::mapToPlayer)
    }

    fun mapToPlayer(resultSet: ResultSet) : List<Player> {
        val result = mutableListOf<Player>()
        while(resultSet.next()) {
            val firstName = resultSet.getString("Play_FirstName")
            val lastName = resultSet.getString("Play_LastName")
            val clubName = resultSet.getString("Club_Name")
            val typeId = resultSet.getInt("Type_ID")
            val typeName = resultSet.getString("Type_Name")
            val price = resultSet.getInt("Type_StartGebuehr")
            result.add(Player(String.format("%s %s", firstName, lastName), clubName, Discipline(typeId, typeName, price)))
        }
        return result
    }
}