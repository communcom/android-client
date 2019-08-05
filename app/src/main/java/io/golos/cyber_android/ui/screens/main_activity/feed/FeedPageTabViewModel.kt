package io.golos.cyber_android.ui.screens.main_activity.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.posts.AbstractPostFeedViewModel
import io.golos.cyber_android.ui.common.widgets.EditorWidget
import io.golos.cyber_android.ui.common.widgets.sorting.SortingWidget
import io.golos.cyber_android.ui.common.widgets.sorting.TimeFilter
import io.golos.cyber_android.ui.common.widgets.sorting.TrendingSort
import io.golos.domain.entities.DiscussionsSort
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.AbstractFeedUseCase
import io.golos.domain.interactors.model.FeedTimeFrameOption
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.interactors.user.UserMetadataUseCase
import io.golos.domain.interactors.user.UserMetadataUseCaseImpl
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import io.golos.domain.requestmodel.QueryResult

abstract class FeedPageTabViewModel<out T : PostFeedUpdateRequest>(
    feedUseCase: AbstractFeedUseCase<out T, PostEntity, PostModel>,
    voteUseCase: VoteUseCase,
    posterUseCase: DiscussionPosterUseCase,
    signInUseCase: SignInUseCase,
    private val userMetadataUseCase: UserMetadataUseCase? = null
) : AbstractPostFeedViewModel<T>(feedUseCase, voteUseCase, posterUseCase, signInUseCase) {

    private val sortingWidgetState = MutableLiveData(
        SortingWidget.SortingWidgetState(TrendingSort.TOP, TimeFilter.PAST_24_HR)
    )

    val getSortingWidgetStateLiveData = sortingWidgetState as LiveData<SortingWidget.SortingWidgetState>

    private val editorWidgetStateLiveData = MediatorLiveData<EditorWidget.EditorWidgetState>().apply {
        userMetadataUseCase?.let {
            addSource(userMetadataUseCase.getAsLiveData) {
                if (it is QueryResult.Success)
                    postValue(EditorWidget.EditorWidgetState(it.originalQuery.personal.avatarUrl, it.originalQuery.username))
            }
        }
    }

    val getEditorWidgetStateLiveData = editorWidgetStateLiveData as LiveData<EditorWidget.EditorWidgetState>

    fun onSearch(query: String) {

    }

    fun onSort(sort: TrendingSort) {
        sortingWidgetState.value = sortingWidgetState.value?.copy(sort = sort)
        requestRefresh()
    }

    fun onFilter(filter: TimeFilter) {
        sortingWidgetState.value = sortingWidgetState.value?.copy(filter = filter)
        requestRefresh()
    }

    init {
        userMetadataUseCase?.subscribe()
        requestRefresh()
    }

    override fun onCleared() {
        super.onCleared()
        userMetadataUseCase?.unsubscribe()
    }

    override fun getFeedSort(): DiscussionsSort? {
        return sortingWidgetState.getSortModel()
    }

    override fun getFeedTimeFrame(): FeedTimeFrameOption? {
        return sortingWidgetState.getTimeFrameModel()
    }

    private fun LiveData<SortingWidget.SortingWidgetState>.getSortModel() =
        if (this.value?.sort == TrendingSort.TOP)
            DiscussionsSort.POPULAR
        else DiscussionsSort.FROM_NEW_TO_OLD

    private fun LiveData<SortingWidget.SortingWidgetState>.getTimeFrameModel() =
        when(this.value?.filter) {
            TimeFilter.PAST_24_HR -> FeedTimeFrameOption.DAY
            TimeFilter.PAST_WEEK -> FeedTimeFrameOption.WEEK
            TimeFilter.PAST_MONTH -> FeedTimeFrameOption.MONTH
            TimeFilter.PAST_YEAR -> FeedTimeFrameOption.YEAR
            TimeFilter.OF_ALL_TIME -> FeedTimeFrameOption.ALL
            null -> FeedTimeFrameOption.ALL
        }
}