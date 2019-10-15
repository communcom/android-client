package io.golos.domain.entities

/**
 * Describe follower
 *
 * @param userId user id
 * @param firstName first name
 * @param lastName last name
 * @param avatarUrl avatar url
 */
data class FollowersDomain(val userId: String,
                           val firstName: String,
                           val lastName: String,
                           val avatarUrl: String)