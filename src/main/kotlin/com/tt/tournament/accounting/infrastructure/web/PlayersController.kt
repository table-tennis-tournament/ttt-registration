package com.tt.tournament.accounting.infrastructure.web

import com.tt.tournament.xmlimport.infrastructure.database.PlayerRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PlayersController(private val playerRepository: PlayerRepository) {

    @GetMapping("/players")
    fun players(
        @RequestParam(required = false, defaultValue = "lastName") sortBy: String,
        @RequestParam(required = false, defaultValue = "asc") order: String,
        model: Model
    ): String {
        val allPlayers = playerRepository.readAllPlayers()

        val sortedPlayers = when (sortBy) {
            "firstName" -> if (order == "desc") allPlayers.sortedByDescending { it.firstName } else allPlayers.sortedBy { it.firstName }
            "lastName" -> if (order == "desc") allPlayers.sortedByDescending { it.lastName } else allPlayers.sortedBy { it.lastName }
            "club" -> if (order == "desc") allPlayers.sortedByDescending { it.club } else allPlayers.sortedBy { it.club }
            "totalAmount" -> if (order == "desc") allPlayers.sortedByDescending { it.totalAmount() } else allPlayers.sortedBy { it.totalAmount() }
            "paid" -> if (order == "desc") allPlayers.sortedByDescending { it.isFullyPaid() } else allPlayers.sortedBy { it.isFullyPaid() }
            else -> allPlayers.sortedBy { it.lastName }
        }

        model.addAttribute("players", sortedPlayers)
        model.addAttribute("sortBy", sortBy)
        model.addAttribute("order", order)

        return "players"
    }
}