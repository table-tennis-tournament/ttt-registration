package com.tt.tournament.infrastructure.report

import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import net.sf.jasperreports.export.SimplePdfExporterConfiguration
import net.sf.jasperreports.export.SimplePdfReportConfiguration
import org.springframework.stereotype.Component

@Component
class SimpleReportExporter{

    fun exportToPdf(fileName: String,  author: String, jasperPrint: JasperPrint) {

        // print report to file
        val exporter = JRPdfExporter()

        exporter.setExporterInput(SimpleExporterInput(jasperPrint))
        exporter.exporterOutput = SimpleOutputStreamExporterOutput(fileName)

        val reportConfig = SimplePdfReportConfiguration()
        reportConfig.isSizePageToContent = true
        reportConfig.isForceLineBreakPolicy = false

        val exportConfig = SimplePdfExporterConfiguration()
        exportConfig.metadataAuthor = "baeldung"
//        exportConfig.isEncrypted = true
        exportConfig.setAllowedPermissionsHint("PRINTING")

        exporter.setConfiguration(reportConfig)
        exporter.setConfiguration(exportConfig)

        exporter.exportReport()
    }

}
