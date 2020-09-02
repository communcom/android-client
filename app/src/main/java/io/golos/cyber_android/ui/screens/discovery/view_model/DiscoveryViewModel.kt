package io.golos.cyber_android.ui.screens.discovery.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.commun4j.services.model.QuickSearchCommunityItem
import io.golos.commun4j.services.model.QuickSearchPostItem
import io.golos.commun4j.services.model.QuickSearchProfileItem
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.dto.*
import io.golos.cyber_android.ui.mappers.mapToPost
import io.golos.cyber_android.ui.screens.discovery.model.DiscoveryModel
import io.golos.cyber_android.ui.screens.profile_followers.dto.FollowersListItem
import io.golos.cyber_android.ui.shared.extensions.getMessage
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.CommunityListItem
import io.golos.data.mappers.*
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.*
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
import io.golos.utils.helpers.toAbsoluteUrl
import io.golos.utils.id.MurmurHash
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class DiscoveryViewModel
@Inject
constructor(
    dispatcherProvider: DispatchersProvider,
    model: DiscoveryModel
) : ViewModelBase<DiscoveryModel>(
    dispatcherProvider,
    model
) {

    private val _communitiesLiveData = MutableLiveData<List<CommunityListItem>>()
    val communities: LiveData<List<CommunityListItem>>
        get() = _communitiesLiveData

    private val _usersLiveData = MutableLiveData<List<FollowersListItem>>()
    val users: LiveData<List<FollowersListItem>>
        get() = _usersLiveData

    private val _postsLiveData = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>>
        get() = _postsLiveData

    private val _searchPreview=MutableLiveData<List<Any>>()
    val searchPreview:LiveData<List<Any>>
        get() = _searchPreview

    fun search(searchString: String) {
        launch {
            try {
                val searchResult = model.search(searchString)
                //                val posts = (searchResult.posts?.items as? List<QuickSearchPostItem>)?.map { it.mapToPostDomain() }
                val communities = (searchResult.communities?.items as? List<QuickSearchCommunityItem>)?.mapIndexed {
                        index, item-> item.mapToCommunity(index,searchResult.communities?.total ?: 0)
                }
                val profileItems = (searchResult.profiles?.items as? List<QuickSearchProfileItem>)?.mapIndexed {
                        index, item -> item.mapToProfile(index,searchResult.profiles?.total ?: 0)
                }
                //                _postsLiveData.postValue(posts)
                _communitiesLiveData.postValue(communities)
                _usersLiveData.postValue(profileItems)
                val list = ArrayList<Any>()
                list.addAll(communities as ArrayList)
                list.addAll(profileItems as ArrayList)
                _searchPreview.postValue(list)
            } catch (e: Exception) {
                Timber.d(e)
                _command.postValue(ShowMessageTextCommand(e.getMessage(App.mInstance.applicationContext)))
            }
        }
    }

    private fun QuickSearchPostItem.mapToPostDomain():Post{
        return PostDomain(
            author = this.author.mapToAuthorDomain(),
            community = this.community.mapToCommunityDomain(),
            contentId = this.contentId.mapToContentIdDomain(),
            body = this.document?.let { JsonToDtoMapper().map(it) }/*null*/,
            meta = this.meta.mapToMetaDomain(),
            stats = this.stats?.mapToStatsDomain(),
            type = this.type,
            shareUrl = this.url.toAbsoluteUrl(),
            votes = this.votes.mapToVotesDomain(),
            isMyPost = false,
            reward = null,
            donation = null,
            viewCount = 0
        ).mapToPost()
    }

    private fun QuickSearchCommunityItem.mapToCommunity(index: Int,lastIndex: Int):CommunityListItem{
        return CommunityListItem(
            id = MurmurHash.hash64(communityId),
            isFirstItem = false,
            version = 0,
            isLastItem = index == lastIndex,
            isProgress = false,
            isInPositiveState =this.isSubscribed?:false,
            isInBlockList = isBlocked?:false,
            community = CommunityDomain(
                communityId = CommunityIdDomain(this.communityId),
                alias = this.alias,
                avatarUrl = this.avatarUrl,
                coverUrl = this.coverUrl,
                isSubscribed = this.isSubscribed?:false,
                name = this.name,
                postsCount = this.postsCount ?: 0,
                subscribersCount = this.subscribersCount
            )
        )
    }

    private fun QuickSearchProfileItem.mapToProfile(index:Int,lastIndex:Int) : FollowersListItem{
        return FollowersListItem(
            id = MurmurHash.hash64(this.userId.name),
            version = 0,
            isFirstItem = false,
            isLastItem = index == lastIndex,
            follower = UserDomain(UserIdDomain(userId.name),
                username?:"",
                avatarUrl,
                this.stats?.postsCount,
                this.subscribers?.usersCount,
                isSubscribed?:false
            ),
            isFollowing = true,
            filter = FollowersFilter.MUTUALS)
    }

    fun invalidateSearchResult() {
        _communitiesLiveData.value = null
        _postsLiveData.value = null
        _searchPreview.value = null
        _usersLiveData.value = null
    }
}