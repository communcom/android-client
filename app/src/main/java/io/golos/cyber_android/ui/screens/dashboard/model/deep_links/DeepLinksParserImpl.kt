package io.golos.cyber_android.ui.screens.dashboard.model.deep_links

import android.net.Uri
import io.golos.cyber_android.ui.screens.dashboard.dto.DeepLinkInfo
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserProfileDomain
import io.golos.domain.repositories.UsersRepository
import io.golos.domain.use_cases.community.CommunitiesRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeepLinksParserImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val usersRepository: UsersRepository,
    private val communitiesRepository: CommunitiesRepository
): DeepLinksParser {
    override suspend fun parse(uri: Uri): DeepLinkInfo? =
        uri.pathSegments
            .let { segments ->
                when(segments.size) {
                    1 -> {
                        if(segments[0].startsWith("@")) {
                            DeepLinkInfo.ProfileDeepLink(usersRepository.getUserProfile(segments[0].replace("@", "")).userId)
                        } else {
                            DeepLinkInfo.CommunityDeepLink(communitiesRepository.getCommunityIdByAlias(segments[0]))
                        }
                    }
                    3 -> {
                        lateinit var communityId: Deferred<CommunityIdDomain>
                        lateinit var userId: Deferred<UserProfileDomain>
                        withContext(dispatchersProvider.ioDispatcher) {
                            communityId = async { communitiesRepository.getCommunityIdByAlias(segments[0]) }
                            userId = async { usersRepository.getUserProfile(segments[1].replace("@", "")) }
                        }

                        DeepLinkInfo.PostDeepLink(
                            communityId = communityId.await(),
                            userId = userId.await().userId,
                            postId = segments[2]
                        )
                    }
                    else -> null
                }
            }
}