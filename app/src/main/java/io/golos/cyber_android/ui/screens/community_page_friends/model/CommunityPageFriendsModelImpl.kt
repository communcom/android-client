package io.golos.cyber_android.ui.screens.community_page_friends.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.community_page.dto.CommunityFriend
import io.golos.cyber_android.ui.screens.community_page_members.dto.CommunityUserListItem
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.extensions.map
import io.golos.domain.utils.MurmurHash
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CommunityPageFriendsModelImpl
@Inject
constructor(
    private val appContext: Context,
    private val dispatchersProvider: DispatchersProvider,
    private val friends: List<CommunityFriend>
) : ModelBaseImpl(), CommunityPageFriendsModel {

    private val items = MutableLiveData<List<VersionedListItem>>(listOf())

    override val title: String
        get() = appContext.resources.getString(R.string.friends)

    override fun getItems(): LiveData<List<VersionedListItem>> = items

    override suspend fun loadPage() {
        val convertedItems = withContext(dispatchersProvider.calculationsDispatcher) {
            friends.mapIndexed { index, item ->
                CommunityUserListItem(
                    id = MurmurHash.hash64(item.userId),
                    version = 0,
                    user = UserDomain(
                        userId = UserIdDomain(item.userId),
                        userName = item.userName,
                        userAvatar = item.avatarUrl,
                        postsCount = 0,
                        followersCount = 0,
                        isSubscribed = true
                    ),
                    isFollowing = true,
                    isLastItem = index == friends.lastIndex,
                    canFollow = false,
                    showPosts = false
                )
            }
        }

        items.value = convertedItems
    }
}