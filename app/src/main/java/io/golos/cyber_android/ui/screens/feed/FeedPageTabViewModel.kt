package io.golos.cyber_android.ui.screens.feed

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.posts.AbstractFeedViewModel
import io.golos.cyber_android.widgets.EditorWidget
import io.golos.cyber_android.widgets.sorting.TimeFilter
import io.golos.cyber_android.widgets.sorting.TrendingSort
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.AbstractFeedUseCase
import io.golos.domain.interactors.feed.CommunityFeedUseCase
import io.golos.domain.model.CommunityFeedUpdateRequest
import io.golos.domain.model.PostFeedUpdateRequest

open class FeedPageTabViewModel<out T : PostFeedUpdateRequest>(feedUseCase: AbstractFeedUseCase<out T>, voteUseCase: VoteUseCase) :
    AbstractFeedViewModel<T>(feedUseCase, voteUseCase) {

    val sortingWidgetState = MutableLiveData<HeadersPostsAdapter.SortingWidgetState>(
        HeadersPostsAdapter.SortingWidgetState(TrendingSort.TOP, TimeFilter.PAST_24_HR)
    )
    val editorWidgetStateLiveData = MutableLiveData<EditorWidget.EditorWidgetState>(
        EditorWidget.EditorWidgetState("https://www.w3schools.com/howto/img_avatar.png")
    )

    fun onSearch(query: String) {

    }

    fun onSort(sort: TrendingSort) {
        sortingWidgetState.postValue(sortingWidgetState.value?.copy(sort = sort))
    }

    fun onFilter(filter: TimeFilter) {
        sortingWidgetState.postValue(sortingWidgetState.value?.copy(filter = filter))
    }
}