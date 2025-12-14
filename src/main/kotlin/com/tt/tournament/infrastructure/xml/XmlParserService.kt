package com.tt.tournament.infrastructure.xml

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.JAXBException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.InputStream
import java.io.StringReader
import javax.xml.parsers.SAXParserFactory

@Service
class XmlParserService {

    private val logger = LoggerFactory.getLogger(XmlParserService::class.java)

    fun parseTournamentXml(xmlContent: String): TournamentDto {
        try {
            val jaxbContext = JAXBContext.newInstance(TournamentDto::class.java)
            val unmarshaller = jaxbContext.createUnmarshaller()

            // Disable external DTD and entity resolution for security
            val saxParserFactory = SAXParserFactory.newInstance()
            saxParserFactory.isNamespaceAware = true
            saxParserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false)
            saxParserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
            saxParserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)

            val saxParser = saxParserFactory.newSAXParser()
            val xmlReader = saxParser.xmlReader
            val source = org.xml.sax.InputSource(StringReader(xmlContent))
            val saxSource = javax.xml.transform.sax.SAXSource(xmlReader, source)

            return unmarshaller.unmarshal(saxSource) as TournamentDto
        } catch (e: JAXBException) {
            logger.error("Failed to parse XML: ${e.message}", e)
            throw XmlParseException("Invalid tournament XML format: ${e.message}", e)
        } catch (e: Exception) {
            logger.error("Failed to parse XML: ${e.message}", e)
            throw XmlParseException("Invalid tournament XML format: ${e.message}", e)
        }
    }

    fun parseTournamentXml(inputStream: InputStream): TournamentDto {
        try {
            val jaxbContext = JAXBContext.newInstance(TournamentDto::class.java)
            val unmarshaller = jaxbContext.createUnmarshaller()

            // Disable external DTD and entity resolution for security
            val saxParserFactory = SAXParserFactory.newInstance()
            saxParserFactory.isNamespaceAware = true
            saxParserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false)
            saxParserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
            saxParserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)

            val saxParser = saxParserFactory.newSAXParser()
            val xmlReader = saxParser.xmlReader
            val source = org.xml.sax.InputSource(inputStream)
            val saxSource = javax.xml.transform.sax.SAXSource(xmlReader, source)

            return unmarshaller.unmarshal(saxSource) as TournamentDto
        } catch (e: JAXBException) {
            logger.error("Failed to parse XML from stream: ${e.message}", e)
            throw XmlParseException("Invalid tournament XML format: ${e.message}", e)
        } catch (e: Exception) {
            logger.error("Failed to parse XML from stream: ${e.message}", e)
            throw XmlParseException("Invalid tournament XML format: ${e.message}", e)
        }
    }
}

class XmlParseException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
