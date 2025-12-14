package com.tt.tournament.infrastructure.db

import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository

@Repository
class TypeRepository(val jdbcClient: JdbcClient) {

    fun findByName(typeName: String): TypeEntity? {
        val sql = "SELECT Type_ID, Type_Name, Type_StartGebuehr, Type_Active FROM type WHERE Type_Name = :typeName AND Type_Active = 1"
        return jdbcClient.sql(sql)
            .param("typeName", typeName)
            .query { rs, _ ->
                TypeEntity(
                    id = rs.getInt("Type_ID"),
                    name = rs.getString("Type_Name"),
                    startGebuehr = rs.getDouble("Type_StartGebuehr"),
                    active = rs.getInt("Type_Active")
                )
            }
            .optional()
            .orElse(null)
    }

    fun findAll(): List<TypeEntity> {
        val sql = "SELECT Type_ID, Type_Name, Type_StartGebuehr, Type_Active FROM type WHERE Type_Active = 1"
        return jdbcClient.sql(sql)
            .query { rs, _ ->
                TypeEntity(
                    id = rs.getInt("Type_ID"),
                    name = rs.getString("Type_Name"),
                    startGebuehr = rs.getDouble("Type_StartGebuehr"),
                    active = rs.getInt("Type_Active")
                )
            }
            .list()
    }

    fun create(typeName: String, startGebuehr: Double = 0.0): Int {
        val sql = """
            INSERT INTO type (Type_Name, Type_StartGebuehr, Type_Active)
            VALUES (:typeName, :startGebuehr, 1)
        """
        jdbcClient.sql(sql)
            .param("typeName", typeName)
            .param("startGebuehr", startGebuehr)
            .update()

        // Get the last inserted ID
        val idSql = "SELECT LAST_INSERT_ID() as id"
        return jdbcClient.sql(idSql)
            .query { rs, _ -> rs.getInt("id") }
            .single()
    }
}

data class TypeEntity(
    val id: Int,
    val name: String,
    val startGebuehr: Double,
    val active: Int
)
