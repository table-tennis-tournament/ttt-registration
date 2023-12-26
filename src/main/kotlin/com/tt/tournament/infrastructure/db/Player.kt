package com.tt.tournament.infrastructure.db

data class Player(val id: Number, val name: String, val club: String, var disciplines: List<Discipline>){
    fun addDisciplines(disciplinesToAdd: List<Discipline>): Player {
        for (disciplineToAdd in disciplinesToAdd) {
            this.disciplines.addLast(disciplineToAdd);
        }
        return this
    }
}
