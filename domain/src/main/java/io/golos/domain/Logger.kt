package io.golos.domain

interface Logger {
    fun log(tag: String, message: String)

    fun log(ex: Throwable)
}