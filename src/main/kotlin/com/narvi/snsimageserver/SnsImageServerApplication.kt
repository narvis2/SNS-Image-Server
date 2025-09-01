package com.narvi.snsimageserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SnsImageServerApplication

fun main(args: Array<String>) {
    runApplication<SnsImageServerApplication>(*args)
}
