package io.golos.cyber_android.ui.screens.profile_black_list.view.list.users

import android.view.ViewGroup
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.LoadingListItem
import io.golos.cyber_android.ui.common.recycler_view.versioned.RetryListItem
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.BlackListFilter
import io.golos.cyber_android.ui.screens.profile_black_list.dto.UserListItem
import io.golos.cyber_android.ui.screens.profile_black_list.view.list.BlackListListItemEventsProcessor
import io.golos.cyber_android.ui.screens.profile_black_list.view.list.LoadingListItemViewHolder

class UsersListAdapter(
    private val listItemEventsProcessor: BlackListListItemEventsProcessor,
    pageSize: Int
) : VersionedListAdapterBase<BlackListListItemEventsProcessor>(listItemEventsProcessor, pageSize) {

    private companion object {
        const val USER = 0
        const val LOADING = 1
        const val RETRY = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBase<BlackListListItemEventsProcessor, VersionedListItem> =
        when(viewType) {
            USER -> UserListItemViewHolder(parent)
            LOADING -> LoadingListItemViewHolder(parent)
            RETRY -> RetryListItemViewHolder(parent)
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun getItemViewType(position: Int): Int =
        when(items[position]) {
            is UserListItem -> USER
            is LoadingListItem -> LOADING
            is RetryListItem -> RETRY
            else -> throw UnsupportedOperationException("This type of item is not supported")
        }

    override fun onNextPageReached() = listItemEventsProcessor.onNextPageReached(BlackListFilter.COMMUNITIES)
}