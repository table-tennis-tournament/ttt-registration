package com.tt.tournament

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class TttRegistrationApplication

fun main(args: Array<String>) {
	runApplication<TttRegistrationApplication>(*args)
}
