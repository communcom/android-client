package io.golos.cyber_android.ui.screens.followers

/**
 * Describe follower
 *
 * @param userId user id
 * @param firstName first name
 * @param lastName last name
 * @param avatarUrl avatar url
 */
data class Follower(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val avatarUrl: String
)