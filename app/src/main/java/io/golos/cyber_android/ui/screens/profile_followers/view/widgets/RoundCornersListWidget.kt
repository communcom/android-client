package io.golos.cyber_android.ui.screens.profile_followers.view.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.versioned.DynamicListWidget
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListAdapterBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import kotlinx.android.synthetic.main.view_round_corners_list.view.*

abstract class RoundCornersListWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr),
    DynamicListWidget {

    private lateinit var listAdapter: VersionedListAdapterBase<*>
    private lateinit var layoutManager: LinearLayoutManager

    init {
        inflate(getContext(), R.layout.view_round_corners_list, this)
    }

    override fun updateList(data: List<VersionedListItem>) {
        if(!::listAdapter.isInitialized) {
            layoutManager = LinearLayoutManager(context)

            listAdapter = getAdapter()
            listAdapter.setHasStableIds(true)

            list.isSaveEnabled = false
            list.itemAnimator = null
            list.layoutManager = layoutManager
            list.adapter = listAdapter
        }

        listAdapter.update(data)
    }

    protected abstract fun getAdapter() : VersionedListAdapterBase<*>
}