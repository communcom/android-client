package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.model

import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.common.recycler_view.ListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.data_repository.dto.CommunityType
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.dto.CommunityListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.dto.LoadingListItem
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.dto.PageLoadResult
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.model.search.CommunitiesSearch
import io.golos.data.api.communities.CommunitiesApi
import io.golos.domain.AppResourcesProvider
import io.golos.domain.commun_entities.Community
import io.golos.domain.extensions.mapSuccess
import io.golos.shared_core.IdUtil
import io.golos.shared_core.MurmurHash
import javax.inject.Inject

class CommunityModelImpl
@Inject
constructor(
    private val communitiesApi: CommunitiesApi,
    private val appResources: AppResourcesProvider,
    private val search: CommunitiesSearch,
    private val communityType: CommunityType
) : ModelBaseImpl(), CommunityModel {

    private var pageSize = 0

    private enum class LoadingStage {
        SHOWING_PROGRESS,
        LOAD_DATA
    }
    private var currentStage = LoadingStage.SHOWING_PROGRESS

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

    override fun close() = search.close()

    override fun search(searchTest: String) = search.search(searchTest)

    @Suppress("NestedLambdaShadowedImplicitParameter")
    override fun setOnSearchResultListener(listener: (Either<List<ListItem>?, Throwable>) -> Unit) {
        search.setOnSearchResultListener {
            it.mapSuccess {
                it?.map { it.map() }
            }
            .let { listener(it) }
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
            communitiesApi.getCommunitiesList(copyItems.size, pageSize, communityType == CommunityType.USER)
                .map { rawItem -> rawItem.map() }
                .let {
                    allDataLoaded = it.size < pageSize

                    copyItems.addAll(it)
                    loadedItems = copyItems

                    currentStage = LoadingStage.SHOWING_PROGRESS

                    PageLoadResult(false, loadedItems)
                }
        } catch(ex: Exception) {
            allDataLoaded = true
            loadedItems = copyItems
            currentStage = LoadingStage.SHOWING_PROGRESS
            PageLoadResult(false, loadedItems)
        }
    }

    private fun Community.map(): ListItem =
        CommunityListItem(
            MurmurHash.hash64(this.id.id),
            this,
            false
        )
}