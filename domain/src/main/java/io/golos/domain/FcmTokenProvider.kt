package io.golos.domain

interface FcmTokenProvider {
    fun provide(): String
}