package com.tt.tournament.infrastructure.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LoginController {

    @GetMapping("/login")
    fun login(): String {
        return "login"
    }

    @GetMapping("/access-denied")
    fun accessDenied(): String {
        return "access-denied"
    }
}
