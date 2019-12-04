package io.golos.domain.use_cases.user

import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.dto.FollowerDomain
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserProfileDomain
import java.io.File

interface UsersRepository {

    /**
     * Get followers in size [pageSizeLimit] as next page with [offset]
     */
    suspend fun getFollowers(query: String?, offset: Int, pageSizeLimit: Int): List<FollowerDomain>

    /**
     * Subscribe to follower
     *
     * @param userId user id
     */
    suspend fun subscribeToFollower(userId: String)

    /**
     * Unsubscribe to follower
     *
     * @param userId user id
     */
    suspend fun unsubscribeToFollower(userId: String)

    suspend fun getUserProfile(user: CyberName): UserProfileDomain

    suspend fun getUserFollowers(user: CyberName, offset: Int, pageSizeLimit: Int): List<UserDomain>

    suspend fun getUserFollowing(user: CyberName, offset: Int, pageSizeLimit: Int): List<UserDomain>

    /**
     * Update cover of current user profile
     * @return url of a cover
     */
    suspend fun updateCover(coverFile: File): String

    /**
     * Update avatar of current user profile
     * @return url of an avatar
     */
    suspend fun updateAvatar(avatarFile: File): String

    /**
     * Clear cover of current user profile
     */
    suspend fun clearCover()

    /**
     * Clear avatar of current user profile
     */
    suspend fun clearAvatar()

    /**
     * Update user's bio
     */
    suspend fun updateBio(bio: String)

    /**
     * Clear bio of current user profile
     */
    suspend fun clearBio()
}