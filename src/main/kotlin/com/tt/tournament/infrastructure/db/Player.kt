package com.tt.tournament.infrastructure.db

data class Player(val id: Number, val firstName: String, val lastName: String, val club: String, val clubCity: String?, var disciplines: MutableList<Discipline>){
    fun addDisciplines(disciplinesToAdd: List<Discipline>): Player {
        for (disciplineToAdd in disciplinesToAdd) {
            this.disciplines.add(disciplineToAdd);
        }
        return this
    }

    fun name() : String {
        return String.format("%s %s", firstName, lastName);
    }
}
