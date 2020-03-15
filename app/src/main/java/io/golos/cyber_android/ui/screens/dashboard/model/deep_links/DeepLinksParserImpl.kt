package io.golos.cyber_android.ui.screens.dashboard.model.deep_links

import android.net.Uri
import io.golos.cyber_android.ui.screens.dashboard.dto.DeepLinkInfo
import io.golos.domain.DispatchersProvider
import io.golos.domain.repositories.UsersRepository
import io.golos.domain.use_cases.community.CommunitiesRepository
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
                            DeepLinkInfo.ProfileDeepLink(getUserId(segments[0].replace("@", "")))
                        } else {
                            DeepLinkInfo.CommunityDeepLink(getCommunityId(segments[0]))
                        }
                    }
                    3 -> {
                        DeepLinkInfo.PostDeepLink(
                            communityId = getCommunityId(segments[0]),
                            userId = getUserId(segments[1].replace("@", "")),
                            postId = segments[2]
                        )
                    }
                    else -> null
                }
            }

    private suspend fun getUserId(userName: String) =
        withContext(dispatchersProvider.ioDispatcher) {
            usersRepository.getUserProfile(userName).userId
        }

    private suspend fun getCommunityId(alias: String) =
        withContext(dispatchersProvider.ioDispatcher) {
            communitiesRepository.getCommunityIdByAlias(alias)
        }
}