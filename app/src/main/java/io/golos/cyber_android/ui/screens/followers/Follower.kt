package io.golos.cyber_android.ui.screens.followers

/**
 * Describe follower
 *
 * @param userId user id
 * @param firstName first name
 * @param lastName last name
 * @param avatarUrl avatar url
 * @param isFollowing true if user following on this user
 */
data class Follower(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val avatarUrl: String,
    var isFollowing: Boolean
)