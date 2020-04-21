package io.golos.domain.dto.notifications

data class NotificationSettingsDomain(
    val type: NotificationTypeDomain,
    val isEnabled: Boolean
)