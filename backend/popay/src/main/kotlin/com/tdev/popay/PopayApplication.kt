package com.tdev.popay

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class PopayApplication

fun main(args: Array<String>) {
	runApplication<PopayApplication>(*args)
}
