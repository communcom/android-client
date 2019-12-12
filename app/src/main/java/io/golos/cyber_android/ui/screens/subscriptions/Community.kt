package io.golos.cyber_android.ui.screens.subscriptions

data class Community(
    val communityId: String,
    val name: String,
    val logo: String?,
    val followersCount: Int,
    var isSubscribed: Boolean
)