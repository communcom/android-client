package io.golos.cyber_android.ui.screens.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.posts.AbstractPostFeedViewModel
import io.golos.cyber_android.widgets.EditorWidget
import io.golos.cyber_android.widgets.sorting.SortingWidget
import io.golos.cyber_android.widgets.sorting.TimeFilter
import io.golos.cyber_android.widgets.sorting.TrendingSort
import io.golos.domain.entities.PostEntity
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.AbstractFeedUseCase
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.interactors.user.UserMetadataUseCase
import io.golos.domain.requestmodel.PostFeedUpdateRequest
import io.golos.domain.requestmodel.QueryResult

abstract class FeedPageTabViewModel<out T : PostFeedUpdateRequest>(
    feedUseCase: AbstractFeedUseCase<out T, PostEntity, PostModel>,
    voteUseCase: VoteUseCase,
    posterUseCase: DiscussionPosterUseCase,
    private val userMetadataUseCase: UserMetadataUseCase? = null
) :
    AbstractPostFeedViewModel<T>(feedUseCase, voteUseCase, posterUseCase) {

    private val sortingWidgetState = MutableLiveData<SortingWidget.SortingWidgetState>(
        SortingWidget.SortingWidgetState(TrendingSort.TOP, TimeFilter.PAST_24_HR)
    )

    val getSortingWidgetStateLiveData = sortingWidgetState as LiveData<SortingWidget.SortingWidgetState>

    private val editorWidgetStateLiveData = MediatorLiveData<EditorWidget.EditorWidgetState>().apply {
        userMetadataUseCase?.let {
            addSource(userMetadataUseCase.getAsLiveData) {
                if (it is QueryResult.Success)
                    postValue(EditorWidget.EditorWidgetState(it.originalQuery.personal.avatarUrl))
            }
        }
    }

    val getEditorWidgetStateLiveData  = editorWidgetStateLiveData as LiveData<EditorWidget.EditorWidgetState>

    fun onSearch(query: String) {

    }

    fun onSort(sort: TrendingSort) {
        sortingWidgetState.postValue(sortingWidgetState.value?.copy(sort = sort))
    }

    fun onFilter(filter: TimeFilter) {
        sortingWidgetState.postValue(sortingWidgetState.value?.copy(filter = filter))
    }

    init {
        userMetadataUseCase?.subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        userMetadataUseCase?.unsubscribe()
    }
}