package io.golos.data.repositories

import io.golos.commun4j.Commun4j
import io.golos.commun4j.abi.implementation.BandWidthProvideOption
import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.api.user.UsersApi
import io.golos.data.mappers.mapToUserProfileDomain
import io.golos.domain.DispatchersProvider
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.FollowerDomain
import io.golos.domain.dto.UserKeyType
import io.golos.domain.dto.UserProfileDomain
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.use_cases.user.UsersRepository
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val usersApi: UsersApi,
    private val dispatchersProvider: DispatchersProvider,
    private val commun4j: Commun4j,
    private val currentUserRepository: CurrentUserRepository,
    private val userKeyStore: UserKeyStore
) : RepositoryBase(dispatchersProvider),
    UsersRepository {

    override suspend fun getFollowers(query: String?, offset: Int, pageSizeLimit: Int): List<FollowerDomain> {
        return withContext(dispatchersProvider.ioDispatcher){
            usersApi.getFollowers(query, offset, pageSizeLimit)
        }
    }

    override suspend fun subscribeToFollower(userId: String) {
        withContext(dispatchersProvider.ioDispatcher){
            usersApi.subscribeToFollower(userId)
        }
    }

    override suspend fun unsubscribeToFollower(userId: String) {
        withContext(dispatchersProvider.ioDispatcher){
            usersApi.unsubscribeToFollower(userId)
        }
    }

    override suspend fun getUserProfile(user: CyberName): UserProfileDomain =
        apiCall { commun4j.getUserProfile(user, null) }.mapToUserProfileDomain()

    /**
     * Update cover of current user profile
     * @return url of a cover
     */
    override suspend fun updateCover(coverFile: File): String {
        val url = apiCallChain { commun4j.uploadImage(coverFile) }
        apiCallChain { commun4j.updateUserMetadata(
            avatarUrl = null,
            coverUrl = url,
            biography = null,
            facebook = null,
            telegram = null,
            whatsapp = null,
            wechat = null,
            bandWidthRequest = BandWidthRequest.bandWidthFromComn,
            clientAuthRequest = null,
            user = currentUserRepository.user,
            key = userKeyStore.getKey(UserKeyType.ACTIVE) ) }

        return url
    }

    /**
     * Update avatar of current user profile
     * @return url of an avatar
     */
    override suspend fun updateAvatar(avatarFile: File): String {
        val url = apiCallChain { commun4j.uploadImage(avatarFile) }
        apiCallChain { commun4j.updateUserMetadata(
            avatarUrl = url,
            coverUrl = null,
            biography = null,
            facebook = null,
            telegram = null,
            whatsapp = null,
            wechat = null,
            bandWidthRequest = BandWidthRequest.bandWidthFromComn,
            clientAuthRequest = null,
            user = currentUserRepository.user,
            key = userKeyStore.getKey(UserKeyType.ACTIVE) ) }

        return url
    }

    /**
     * Update user's bio
     */
    override suspend fun updateBio(bio: String) {
        apiCallChain { commun4j.updateUserMetadata(
            avatarUrl = null,
            coverUrl = null,
            biography = bio,
            facebook = null,
            telegram = null,
            whatsapp = null,
            wechat = null,
            bandWidthRequest = BandWidthRequest.bandWidthFromComn,
            clientAuthRequest = null,
            user = currentUserRepository.user,
            key = userKeyStore.getKey(UserKeyType.ACTIVE) ) }
    }
}