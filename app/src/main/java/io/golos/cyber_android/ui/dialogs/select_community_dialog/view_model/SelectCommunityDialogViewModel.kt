package io.golos.cyber_android.ui.dialogs.select_community_dialog.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dialogs.select_community_dialog.dto.CommunitySelected
import io.golos.cyber_android.ui.dialogs.select_community_dialog.model.SelectCommunityDialogModel
import io.golos.cyber_android.ui.screens.communities_list.view.list.CommunityListItemEventsProcessor
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.extensions.fold
import kotlinx.coroutines.launch
import javax.inject.Inject

class SelectCommunityDialogViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: SelectCommunityDialogModel
) : ViewModelBase<SelectCommunityDialogModel>(dispatchersProvider, model), CommunityListItemEventsProcessor {

    val items: LiveData<List<VersionedListItem>>
        get()  = model.items

    val pageSize: Int = model.pageSize

    private var searchString = ""

    private val _searchResultVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val searchResultVisibility: LiveData<Boolean>
        get() = _searchResultVisibility

    private val _searchResultItems: MutableLiveData<List<VersionedListItem>> = MutableLiveData(listOf())
    val searchResultItems: LiveData<List<VersionedListItem>>
        get() = _searchResultItems

    private val _isSearchStringEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    val isSearchStringEnabled: LiveData<Boolean>
        get() = _isSearchStringEnabled

    init {
        model.setOnSearchResultListener { processSearchResult(it) }
        loadPage()
    }

    fun onSearchStringUpdated(searchString: String) {
        if(searchString == this.searchString) {
            return
        }

        this.searchString = searchString
        model.search(searchString)
    }

    override fun onCleared() {
        super.onCleared()
        model.close()
    }

    override fun onItemClick(community: CommunityDomain) {
        _command.value =
            CommunitySelected(community)
    }

    override fun onNextPageReached() = loadPage()

    override fun retry() {
        launch {
            model.retry()
        }
    }

    private fun loadPage() {
        launch {
            model.loadPage()
        }
    }

    private fun processSearchResult(searchResult: Either<List<VersionedListItem>?, Throwable>) {
        searchResult.fold({ resultList ->                   // Success
            if(resultList != null) {
                _searchResultItems.value = resultList
                _searchResultVisibility.value = true
            } else {
                _searchResultItems.value = listOf()
                _searchResultVisibility.value = false
            }                                               // Fail
        }, {
            _command.value = ShowMessageResCommand(R.string.common_general_error)
            _searchResultItems.value = listOf()
            _searchResultVisibility.value = false
        })
    }
}