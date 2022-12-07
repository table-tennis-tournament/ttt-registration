package com.tt.tournament.infrastructure.web

import com.tt.tournament.infrastructure.report.SimpleReportExporter
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.util.JRSaver
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.io.InputStream
import java.util.*
import javax.sql.DataSource


@RestController
class ReportingController(val reportExporter: SimpleReportExporter, val dataSource: DataSource) {


    @GetMapping("/reports")
    fun getReport() {
        val employeeReportStream: InputStream = javaClass.getResourceAsStream("/reports/test.jrxml")
        val jasperReport: JasperReport = JasperCompileManager.compileReport(employeeReportStream)
//        JRSaver.saveObject(jasperReport, "test.jasper")
        val jasperPrint = JasperFillManager.fillReport(jasperReport, HashMap(), dataSource.connection)
        reportExporter.exportToPdf("test.pdf", "test", jasperPrint)
    }

}
