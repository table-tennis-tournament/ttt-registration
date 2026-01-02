package com.tt.tournament.accounting.infrastructure.api

import com.tt.tournament.accounting.application.ReportService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ReportingController(val reportService: ReportService) {


    @GetMapping("/sunday-report")
    fun getSundayReport(): ResponseEntity<ByteArray> {
        val pdfBytes = reportService.generateSundayReportBytes()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_PDF
        headers.setContentDispositionFormData("attachment", "quittungen_sonntag.pdf")
        return ResponseEntity.ok().headers(headers).body(pdfBytes)
    }

    @GetMapping("/saturday-report")
    fun getSaturdayReport(): ResponseEntity<ByteArray> {
        val pdfBytes = reportService.generateSaturdayReportBytes()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_PDF
        headers.setContentDispositionFormData("attachment", "quittungen_samstag.pdf")
        return ResponseEntity.ok().headers(headers).body(pdfBytes)
    }

    @GetMapping("/player-lists")
    fun getPlayerLists(): ResponseEntity<ByteArray> {
        val pdfBytes = reportService.generatePlayerListsBytes()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_PDF
        headers.setContentDispositionFormData("attachment", "spielerliste.pdf")
        return ResponseEntity.ok().headers(headers).body(pdfBytes)
    }

    @GetMapping("/blank-receipt")
    fun getBlankReceipt(): ResponseEntity<ByteArray> {
        val pdfBytes = reportService.generateBlankReceiptBytes()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_PDF
        headers.setContentDispositionFormData("attachment", "blanko_quittung.pdf")
        return ResponseEntity.ok().headers(headers).body(pdfBytes)
    }

    @GetMapping("/double-lists")
    fun getDoubleLists(): ResponseEntity<ByteArray> {
        val pdfBytes = reportService.generateDoubleListsBytes()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_PDF
        headers.setContentDispositionFormData("attachment", "doppellisten.pdf")
        return ResponseEntity.ok().headers(headers).body(pdfBytes)
    }

}