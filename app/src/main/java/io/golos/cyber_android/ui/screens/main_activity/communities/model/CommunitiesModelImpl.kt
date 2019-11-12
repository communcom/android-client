package io.golos.cyber_android.ui.screens.main_activity.communities.model

import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.dto.CommunityListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.dto.LoadingListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.dto.PageLoadResult
import io.golos.data.api.communities.CommunitiesApi
import io.golos.domain.AppResourcesProvider
import io.golos.domain.dto.CommunityDomain
import io.golos.domain.utils.IdUtil
import io.golos.domain.utils.MurmurHash
import javax.inject.Inject

class CommunitiesModelImpl
@Inject
constructor(
    private val communitiesApi: CommunitiesApi,
    private val appResources: AppResourcesProvider
) : ModelBaseImpl(), CommunitiesModel {

    private var pageSize = 0

    private enum class LoadingStage {
        SHOWING_PROGRESS,
        LOAD_DATA
    }
    private var currentStage =
        LoadingStage.SHOWING_PROGRESS

    private var loadedItems: List<ListItem> = listOf()

    private var allDataLoaded = false

    override fun initModel(controlHeight: Int) {
        if(pageSize > 0) {
            return
        }

        val rowHeight = appResources.getDimens(R.dimen.height_community_list_item)
        pageSize = (3 * (controlHeight / rowHeight).toInt())+1
    }

    override fun canLoad(lastVisibleItemPosition: Int): Boolean =
        !allDataLoaded && lastVisibleItemPosition >= loadedItems.size - pageSize / 3

    override suspend fun getPage(lastVisibleItemPosition: Int): PageLoadResult {
        if(allDataLoaded) {
            return PageLoadResult(false, null)
        }

        return when (currentStage) {
            LoadingStage.SHOWING_PROGRESS -> showProgressIndicator()
            LoadingStage.LOAD_DATA -> showItems()
        }
    }

    private fun showProgressIndicator(): PageLoadResult {
        val copyItems = loadedItems.toMutableList()
        copyItems.add(LoadingListItem(IdUtil.generateLongId()))     // Loading indicator has been added

        loadedItems = copyItems

        currentStage = LoadingStage.LOAD_DATA
        return PageLoadResult(true, loadedItems)
    }

    private suspend fun showItems(): PageLoadResult {
        val copyItems = loadedItems.toMutableList()
        copyItems.removeAt(copyItems.indices.last)          // Loading indicator has been removed

        return try {
            communitiesApi.getCommunitiesList(copyItems.size, pageSize, true)
                .map { rawItem -> rawItem.map() }
                .let {
                    allDataLoaded = it.size < pageSize

                    copyItems.addAll(it)
                    loadedItems = copyItems

                    currentStage =
                        LoadingStage.SHOWING_PROGRESS

                    PageLoadResult(false, loadedItems)
                }
        } catch(ex: Exception) {
            allDataLoaded = true
            loadedItems = copyItems
            currentStage =
                LoadingStage.SHOWING_PROGRESS
            PageLoadResult(false, loadedItems)
        }
    }

    private fun CommunityDomain.map(): ListItem =
        CommunityListItem(
            MurmurHash.hash64(this.communityId),
            this,
            false
        )
}