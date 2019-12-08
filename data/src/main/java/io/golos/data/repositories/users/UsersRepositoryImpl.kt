package io.golos.data.repositories.users

import io.golos.commun4j.Commun4j
import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.model.ClientAuthRequest
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.api.user.UsersApi
import io.golos.data.mappers.mapToUserDomain
import io.golos.data.mappers.mapToFollowingUserDomain
import io.golos.data.mappers.mapToUserProfileDomain
import io.golos.data.repositories.RepositoryBase
import io.golos.domain.DispatchersProvider
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.*
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

    override suspend fun subscribeToFollower(userId: UserIdDomain) {
        apiCallChain { commun4j.pinUser(
            pinning = CyberName(userId.userId),
            pinner = CyberName(currentUserRepository.userId.userId),
            bandWidthRequest = BandWidthRequest.bandWidthFromComn,
            clientAuthRequest = ClientAuthRequest.empty,
            key = userKeyStore.getKey(UserKeyType.ACTIVE)
        )}
    }

    override suspend fun unsubscribeToFollower(userId: UserIdDomain) {
        apiCallChain {
            commun4j.unpinUser(
                unpinning = CyberName(userId.userId),
                pinner = CyberName(currentUserRepository.userId.userId),
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                key = userKeyStore.getKey(UserKeyType.ACTIVE)
        )}
    }


    override suspend fun getUserProfile(userId: UserIdDomain): UserProfileDomain =
        apiCall { commun4j.getUserProfile(CyberName(userId.userId), null) }.mapToUserProfileDomain()

    override suspend fun getUserFollowers(userId: UserIdDomain, offset: Int, pageSizeLimit: Int): List<UserDomain> =
        apiCall { commun4j.getSubscribers(CyberName(userId.userId), null, pageSizeLimit, offset) }.items.map { it.mapToUserDomain() }

    override suspend fun getUserFollowing(userId: UserIdDomain, offset: Int, pageSizeLimit: Int): List<FollowingUserDomain> =
        apiCall { commun4j.getUserSubscriptions(CyberName(userId.userId), pageSizeLimit, offset) }.items.map { it.mapToFollowingUserDomain() }

    /**
     * Update cover of current user profile
     * @return url of a cover
     */
    override suspend fun updateCover(coverFile: File): String =
        apiCallChain { commun4j.uploadImage(coverFile) }
            .also { url ->  updateCurrentUserMetadata { it.copy(coverUrl = url) } }

    /**
     * Update avatar of current user profile
     * @return url of an avatar
     */
    override suspend fun updateAvatar(avatarFile: File): String =
        apiCallChain { commun4j.uploadImage(avatarFile) }
            .also { url ->  updateCurrentUserMetadata { it.copy(avatarUrl = url) } }

    /**
     * Clear cover of current user profile
     */
    override suspend fun clearCover() {
        updateCurrentUserMetadata { it.copy(coverUrl = "") }
    }

    /**
     * Clear avatar of current user profile
     */
    override suspend fun clearAvatar() {
        updateCurrentUserMetadata { it.copy(avatarUrl = "") }
    }

    /**
     * Update user's bio
     */
    override suspend fun updateBio(bio: String) {
        updateCurrentUserMetadata { it.copy(biography = bio) }
    }

    /**
     * Clear bio of current user profile
     */
    override suspend fun clearBio() {
        updateCurrentUserMetadata { it.copy(biography = "") }
    }

    private suspend fun updateCurrentUserMetadata(requestAction: (UserMetadataRequest) -> UserMetadataRequest) =
        requestAction(
            UserMetadataRequest(
                avatarUrl = null,
                coverUrl = null,
                biography = null,
                facebook = null,
                telegram = null,
                whatsapp = null,
                wechat = null,
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty,
                user = CyberName(currentUserRepository.authState!!.user.userId),
                key = userKeyStore.getKey(UserKeyType.ACTIVE))
        )
        .let {
            apiCallChain { commun4j.updateUserMetadata(
                avatarUrl = it.avatarUrl,
                coverUrl = it.coverUrl,
                biography = it.biography,
                facebook = it.facebook,
                telegram = it.telegram,
                whatsapp = it.whatsapp,
                wechat = it.wechat,
                bandWidthRequest = it.bandWidthRequest,
                clientAuthRequest = it.clientAuthRequest,
                user = it.user,
                key = it.key) }
        }
}