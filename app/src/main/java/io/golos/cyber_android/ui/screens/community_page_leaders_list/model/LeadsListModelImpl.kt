package io.golos.cyber_android.ui.screens.community_page_leaders_list.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.community_page_leaders_list.dto.*
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.data.exceptions.ApiResponseErrorException
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.CommunityLeaderDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.use_cases.community.CommunitiesRepository
import io.golos.utils.id.IdUtil
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class LeadsListModelImpl
@Inject
constructor(
    private val communityId: CommunityIdDomain,
    private val communitiesRepository: CommunitiesRepository
) : ModelBaseImpl(),
    LeadsListModel {

    private enum class LoadingState {
        READY_TO_LOAD,
        LOADING,
        ALL_DATA_LOADED,
        IN_ERROR
    }

    private var currentLoadingState = LoadingState.READY_TO_LOAD

    private var loadedItems: MutableList<VersionedListItem> = mutableListOf()

    private val _items = MutableLiveData<List<VersionedListItem>>(listOf())
    override val items: LiveData<List<VersionedListItem>>
        get() = _items

    private var votingInProgress = false

    override suspend fun loadLeaders() {
        if (currentLoadingState == LoadingState.READY_TO_LOAD) {
            loadData(false)
        }
    }

    override suspend fun retry() {
        if (currentLoadingState == LoadingState.IN_ERROR) {
            loadData(true)
        }
    }

    override suspend fun vote(leader: UserIdDomain): VoteResult {
        if(votingInProgress) {
            return VoteResult(VoteResultType.VOTE_IN_PROGRESS)
        }

        val leaderIndex = loadedItems.indexOfFirst { it is LeaderListItem && it.userId == leader }
        if(leaderIndex == -1) {
            return VoteResult(VoteResultType.FAIL)
        }

        if((loadedItems[leaderIndex] as LeaderListItem).isVoted) {
            return VoteResult(VoteResultType.FAIL)
        }

        if(loadedItems.any { it is LeaderListItem && it.isVoted }) {
            return VoteResult(VoteResultType.UNVOTE_NEEDED)
        }

        votingInProgress = true

        updateLeader(leaderIndex) { it.copy(isVoted = !it.isVoted, version = it.version+1) }

        try {
            communitiesRepository.voteForLeader(communityId, leader)
        } catch (ex: Exception) {
            Timber.e(ex)
            updateLeader(leaderIndex) { it.copy(isVoted = !it.isVoted, version = it.version+1) }
            return VoteResult(VoteResultType.FAIL, (ex as? ApiResponseErrorException)?.errorInfo?.message)
        } finally {
            votingInProgress = false
        }

        return VoteResult(VoteResultType.SUCCESS)
    }

    override suspend fun unvote(leader: UserIdDomain): VoteResult {
        if(votingInProgress) {
            return VoteResult(VoteResultType.VOTE_IN_PROGRESS)
        }

        val leaderIndex = loadedItems.indexOfFirst { it is LeaderListItem && it.userId == leader }
        if(leaderIndex == -1) {
            return VoteResult(VoteResultType.FAIL)
        }

        if(!(loadedItems[leaderIndex] as LeaderListItem).isVoted) {
            return VoteResult(VoteResultType.FAIL)
        }

        votingInProgress = true

        updateLeader(leaderIndex) { it.copy(isVoted = !it.isVoted, version = it.version+1) }

        try {
            communitiesRepository.unvoteForLeader(communityId, leader)
        } catch (ex: Exception) {
            Timber.e(ex)
            updateLeader(leaderIndex) { it.copy(isVoted = !it.isVoted, version = it.version+1) }
            return VoteResult(VoteResultType.FAIL, (ex as? ApiResponseErrorException)?.errorInfo?.message)
        } finally {
            votingInProgress = false
        }

        return VoteResult(VoteResultType.SUCCESS)
    }

    private suspend fun loadData(isRetry: Boolean) {
        currentLoadingState = LoadingState.LOADING

        if(isRetry) {
            updateData { it[0] = LoadingListItem(IdUtil.generateLongId(), 0) }
        } else {
            updateData { it.add(LoadingListItem(IdUtil.generateLongId(), 0)) }
        }

        var data = getData()

        currentLoadingState = if(data != null) {
           if(data.isEmpty()) {
               data = listOf(EmptyListItem(IdUtil.generateLongId(), 0))
           }

            updateData { leadersList ->
                leadersList.removeAt(0)
                leadersList.addAll(data)
                leadersList.removeIf{
                    it is LeadersHeaderItem || it is NomineesHeaderItem
                }
                val firstTopLeader = leadersList.find { it is LeaderListItem && it.isTop}
                val firstTopLeaderPosition = firstTopLeader?.let { leadersList.indexOf(it) } ?: 0
                leadersList.add(firstTopLeaderPosition, LeadersHeaderItem(0, IdUtil.generateLongId()))
                val firstNominees = leadersList.find { it is LeaderListItem && !it.isTop}
                firstNominees?.let {
                    val firstNomineesPosition = leadersList.indexOf(it)
                    if(firstNomineesPosition != -1){
                        leadersList.add(firstNomineesPosition, NomineesHeaderItem(0, IdUtil.generateLongId()))
                    }
                }

            }

            LoadingState.ALL_DATA_LOADED
        } else {
            updateData { it[0] = RetryListItem(IdUtil.generateLongId(), 0) }
            LoadingState.IN_ERROR
        }
    }

    private suspend fun getData(): List<VersionedListItem>? =
        try {
            communitiesRepository
                .getCommunityLeads(communityId)
                .map { rawItem -> rawItem.map() }
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }


    private fun updateData(updateAction: (MutableList<VersionedListItem>) -> Unit) {
        updateAction(loadedItems)
        _items.value = loadedItems
    }

    private fun updateLeader(leaderIndex: Int, updateAction: (LeaderListItem) -> LeaderListItem) = updateData { list ->
        list[leaderIndex] = updateAction(list[leaderIndex] as LeaderListItem)
    }

    private fun CommunityLeaderDomain.map(): LeaderListItem =
        LeaderListItem(
            id = IdUtil.generateLongId(),
            version = 0,
            userId = userId,
            avatarUrl = avatarUrl,
            username = username,
            rating = rating,
            ratingPercent = ratingPercent,
            isVoted = isVoted,
            isTop = isTop
    )
}