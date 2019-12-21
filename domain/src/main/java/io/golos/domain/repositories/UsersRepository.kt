package io.golos.domain.repositories

import io.golos.domain.dto.*
import io.golos.domain.dto.bc_profile.BCProfileDomain
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
    suspend fun subscribeToFollower(userId: UserIdDomain)

    /**
     * Unsubscribe to follower
     *
     * @param userId user id
     */
    suspend fun unsubscribeToFollower(userId: UserIdDomain)

    suspend fun getUserProfile(userId: UserIdDomain): UserProfileDomain

    suspend fun getUserProfile(userName: String): UserProfileDomain

    suspend fun getUserFollowers(userId: UserIdDomain, offset: Int, pageSizeLimit: Int): List<FollowingUserDomain>

    suspend fun getUserFollowing(userId: UserIdDomain, offset: Int, pageSizeLimit: Int): List<FollowingUserDomain>

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

    suspend fun getUsersInBlackList(offset: Int, pageSize: Int, userId: UserIdDomain): List<UserDomain>

    suspend fun moveUserToBlackList(userId: UserIdDomain)

    suspend fun moveUserFromBlackList(userId: UserIdDomain)
    /**
     * Set ftue board stage
     */
    suspend fun setFtueBoardStage(stage: FtueBoardStageDomain)

    /**
     * Get ftue board stage
     *
     * @return ftue board stage
     */
    suspend fun getFtueBoardStage(): FtueBoardStageDomain

    /**
     * Check that need show ftue board
     *
     * @return true if need show ftue board
     */
    suspend fun isNeedShowFtueBoard(): Boolean

    /**
     * Clear current user data, need use this method for logout
     */
    suspend fun clearCurrentUserData()
}