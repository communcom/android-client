package io.golos.cyber_android.ui.screens.main_activity.communities.data_repository.dto

/**
 * A community entity from external source
 */
data class CommunityExt(
    val id: String,
    val name: String,
    val followersQuantity: Int,
    val logoUrl: String
)