package com.tt.tournament.xmlimport.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application.import")
data class ImportProperties(
    val saturday: String,
    val sunday: String
)