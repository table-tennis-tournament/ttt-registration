package com.tt.tournament.infrastructure.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ImportController {

    @GetMapping("/import")
    fun importPage(): String {
        return "import"
    }
}
