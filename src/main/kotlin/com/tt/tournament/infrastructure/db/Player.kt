package com.tt.tournament.infrastructure.db

data class Player(val id: Number, val name: String, val club: String, var discipline: List<Discipline>)
