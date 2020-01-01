package io.golos.cyber_android.ui.dialogs.select_community_dialog.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.communities_list.view.list.CommunityListItemEventsProcessor
import kotlinx.android.synthetic.main.fragment_communities_select_dialog_list.view.*

@Suppress("unused")
class SelectCommunityDialogList
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var hasPages = true

    private var pageSize = 25

    private lateinit var listAdapter: CommunityListAdapterDialog
    private lateinit var listLayoutManager: LinearLayoutManager

    init {
        inflate(context, R.layout.fragment_communities_select_dialog_list, this)
        attrs?.let { retrieveAttributes(it) }
    }

    fun updateList(data: List<VersionedListItem>, listItemEventsProcessor: CommunityListItemEventsProcessor) {
        if(!::listAdapter.isInitialized) {
            listLayoutManager = LinearLayoutManager(context)

            listAdapter = CommunityListAdapterDialog(listItemEventsProcessor, if(hasPages) pageSize else null)
            listAdapter.setHasStableIds(true)

            itemsList.isSaveEnabled = false
            itemsList.itemAnimator = null
            itemsList.layoutManager = listLayoutManager
            itemsList.adapter = listAdapter
        }

        listAdapter.update(data)
    }

    private fun retrieveAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectCommunityDialogList)
        hasPages = typedArray.getBoolean(R.styleable.SelectCommunityDialogList_has_paging, false)
        typedArray.recycle()
    }
}
