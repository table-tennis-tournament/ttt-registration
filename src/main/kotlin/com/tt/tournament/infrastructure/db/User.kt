package com.tt.tournament.infrastructure.db

data class User(
    val username: String,
    val password: String,
    val enabled: Boolean,
    val authorities: MutableList<Authority> = mutableListOf()
)
