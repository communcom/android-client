package io.golos.cyber_android.ui.screens.main_activity.communities.select_community_dialog

import androidx.lifecycle.MutableLiveData
import io.golos.cyber4j.sharedmodel.Either
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.model.CommunityModel
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.view.list.CommunityListItemEventsProcessor
import io.golos.domain.AppResourcesProvider
import io.golos.domain.DispatchersProvider
import io.golos.domain.extensions.fold
import kotlinx.coroutines.launch
import javax.inject.Inject

class SelectCommunityDialogViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: CommunityModel,
    appResourcesProvider: AppResourcesProvider
) : ViewModelBase<CommunityModel>(dispatchersProvider, model), CommunityListItemEventsProcessor {

    var searchString = ""
        private set

    val items: MutableLiveData<List<ListItem>> = MutableLiveData(listOf())

    val isScrollEnabled: MutableLiveData<Boolean> = MutableLiveData(false)

    val searchResultVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val searchResultItems: MutableLiveData<List<ListItem>> = MutableLiveData(listOf())

    init {
        model.initModel(appResourcesProvider.getDimens(R.dimen.select_community_dialog_height).toInt())
        model.setOnSearchResultListener { processSearchResult(it) }
        loadPage(0)
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

    override fun onItemClick(externalId: String) {
        // do nothing
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