package io.golos.cyber_android.ui.screens.profile_black_list.view.widgets

import android.content.Context
import android.util.AttributeSet
import io.golos.cyber_android.ui.screens.profile_black_list.view.list.users.UsersListAdapter
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListAdapterBase

class UsersListWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CommunitiesListWidget(context, attrs, defStyleAttr) {

    override fun getAdapter() : VersionedListAdapterBase<*> = UsersListAdapter(listItemEventsProcessor, pageSize)
}