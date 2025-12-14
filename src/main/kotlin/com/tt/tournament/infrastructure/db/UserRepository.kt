package com.tt.tournament.infrastructure.db

import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class UserRepository(val jdbcClient: JdbcClient) {

    fun findByUsername(username: String): User? {
        val userSql = "SELECT username, password, enabled FROM users WHERE username = ?"

        val users = jdbcClient.sql(userSql)
            .param(username)
            .query { rs, _ ->
                User(
                    username = rs.getString("username"),
                    password = rs.getString("password"),
                    enabled = rs.getBoolean("enabled")
                )
            }
            .list()

        if (users.isEmpty()) {
            return null
        }

        val user = users[0]

        // Fetch authorities for this user
        val authoritiesSql = "SELECT username, authority FROM authorities WHERE username = ?"
        val authorities = jdbcClient.sql(authoritiesSql)
            .param(username)
            .query { rs, _ ->
                Authority(
                    username = rs.getString("username"),
                    authority = rs.getString("authority")
                )
            }
            .list()

        user.authorities.addAll(authorities)
        return user
    }
}
