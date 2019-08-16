package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.viewModel

import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.model.CommunityModel
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.view.list.CommunityListItemEventsProcessor
import io.golos.domain.DispatchersProvider
import io.golos.domain.extensions.fold
import io.golos.sharedmodel.Either
import kotlinx.coroutines.launch
import javax.inject.Inject

class CommunityViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: CommunityModel
) : ViewModelBase<CommunityModel>(dispatchersProvider, model), CommunityListItemEventsProcessor {

    private var isSetup = false

    var searchString = ""
        private set

    val items: MutableLiveData<List<ListItem>> = MutableLiveData(listOf())

    val isScrollEnabled: MutableLiveData<Boolean> = MutableLiveData(false)

    val searchResultVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val searchResultItems: MutableLiveData<List<ListItem>> = MutableLiveData(listOf())

    init {
        model.setOnSearchResultListener { processSearchResult(it) }
    }

    fun onViewCreated() {
        isSetup = false
    }

    fun onActive(controlHeight: Int) {
        model.initModel(controlHeight)

        if(!isSetup) {
            loadPage(0)
            isSetup = true
        }
    }

    fun onScroll(lastVisibleItemPosition: Int) {
        if(!model.canLoad(lastVisibleItemPosition)) {
            return
        }

        isScrollEnabled.value = false
        loadPage(lastVisibleItemPosition)
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

    private fun loadPage(lastVisibleItemPosition: Int) {
        launch {
            val page = model.getPage(lastVisibleItemPosition)

            page.fold({ pageInfo ->
                pageInfo.data?.let { list -> items.value = list }

                if(pageInfo.hasNextData) {
                    loadPage(lastVisibleItemPosition)
                } else {
                    isScrollEnabled.value = true
                }
            }, {
                command.value = ShowMessageCommand(R.string.common_general_error)
                it.data?.let { list -> items.value = list }
                isScrollEnabled.value = true
            })
        }
    }

    private fun processSearchResult(searchResult: Either<List<ListItem>?, Throwable>) {
        searchResult.fold({ resultList ->                   // Success
            if(resultList != null) {
                searchResultItems.value = resultList
                searchResultVisibility.value = true
            } else {
                searchResultItems.value = listOf()
                searchResultVisibility.value = false
            }                                               // Fail
        }, {
            command.value = ShowMessageCommand(R.string.common_general_error)
            searchResultItems.value = listOf()
            searchResultVisibility.value = false
        })
    }
}