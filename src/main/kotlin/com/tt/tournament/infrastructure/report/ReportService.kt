package com.tt.tournament.infrastructure.report

import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.xhtmlrenderer.pdf.ITextRenderer
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


private const val IMAGE_BASE_64 =
    "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"

@Service
class ReportService {

    public fun generateReport() {
        val receiptsTemplate = parseThymeleafTemplate()
        generatePdfFromHtml(receiptsTemplate);
    }

    private fun parseThymeleafTemplate(): String {
        val templateResolver = ClassLoaderTemplateResolver()
        templateResolver.suffix = ".html"
        templateResolver.templateMode = TemplateMode.HTML
        val templateEngine = TemplateEngine()
        templateEngine.setTemplateResolver(templateResolver)
        val context = Context()
        context.setVariable("base64Image", IMAGE_BASE_64)
        context.setVariable("name", "Fritz Maier")
        context.setVariable("club", "TTV Grün-Weiss Ettlingen")
        context.setVariable("number1", "1.")
        context.setVariable("discipline1", "Herren B")
        context.setVariable("price1", "11€")
        context.setVariable("number2", "2.")
        context.setVariable("discipline2", "Herren C")
        context.setVariable("price2", "11€")
        context.setVariable("sum", "22€")
        return templateEngine.process("receipt", context)
    }

    fun generatePdfFromHtml(html: String?) {
        val outputFolder = (System.getProperty("user.home") + File.separator) + "thymeleaf2.pdf"
        println(System.getProperty("user.home"))
        val outputStream: OutputStream = FileOutputStream(outputFolder)
        val renderer = ITextRenderer()
        renderer.setDocumentFromString(html)
        renderer.layout()
        renderer.createPDF(outputStream)
        outputStream.close()
    }
}