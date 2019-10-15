package io.golos.domain.entities

/**
 * Describe follower
 *
 * @param userId user id
 * @param firstName first name
 * @param lastName last name
 * @param avatarUrl avatar url
 * @param isFollowing true if user following on this user
 */
data class FollowerDomain(val userId: String,
                          val firstName: String,
                          val lastName: String,
                          val avatarUrl: String,
                          val isFollowing: Boolean
)