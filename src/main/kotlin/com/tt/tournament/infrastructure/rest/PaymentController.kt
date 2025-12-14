package com.tt.tournament.infrastructure.rest

import com.tt.tournament.infrastructure.db.PlayerRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class UpdatePaymentRequest(val playerId: Int, val paid: Boolean)
data class UpdatePaymentResponse(val success: Boolean, val message: String)

@RestController
class PaymentController(private val playerRepository: PlayerRepository) {

    @PostMapping("/players/payment")
    fun updatePayment(@RequestBody request: UpdatePaymentRequest): ResponseEntity<UpdatePaymentResponse> {
        return try {
            val rowsUpdated = playerRepository.updatePaymentStatus(request.playerId, request.paid)
            if (rowsUpdated > 0) {
                ResponseEntity.ok(UpdatePaymentResponse(true, "Payment status updated successfully"))
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(UpdatePaymentResponse(false, "Player not found"))
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(UpdatePaymentResponse(false, "Failed to update payment status: ${e.message}"))
        }
    }
}
