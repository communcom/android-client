package io.golos.domain

interface FcmTokenProvider {
    suspend fun provide(): String
}