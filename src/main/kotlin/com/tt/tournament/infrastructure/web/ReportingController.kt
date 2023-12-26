package com.tt.tournament.infrastructure.web

import com.tt.tournament.infrastructure.report.ReportService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ReportingController(val reportService: ReportService) {


    @GetMapping("/sunday-report")
    fun getSundayReport() {
        reportService.generateSundayReport()
    }

    @GetMapping("/saturday-report")
    fun getSaturdayReport() {
        reportService.generateSaturdayReport()
    }

}
