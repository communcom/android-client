package io.golos.data.repositories.settings

import io.golos.domain.dto.ConfigDomain

interface SettingsRepository {
    suspend fun getConfig(): ConfigDomain
}