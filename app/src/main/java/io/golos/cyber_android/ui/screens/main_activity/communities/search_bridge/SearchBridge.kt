package io.golos.cyber_android.ui.screens.main_activity.communities.search_bridge

import android.util.Log
import io.golos.domain.dependency_injection.scopes.FragmentScope
import javax.inject.Inject

/**
 * Link between childs and parent fragments for search string synchronization
 */
@FragmentScope
class SearchBridge
@Inject
constructor(): SearchBridgeParent, SearchBridgeChild {

    init {
        Log.d("", "")
    }

    private val childs = mutableMapOf<Int, ChildSearchFragment>()

    private lateinit var parent: ParentSearchFragment

    override fun attachParent(parent: ParentSearchFragment) {
        this.parent = parent
    }

    override fun attachChild(position: Int, child: ChildSearchFragment) {
        childs[position] = child
    }

    override fun getChild(position: Int): ChildSearchFragment =
        childs[position]!!

    override fun getParent(): ParentSearchFragment = parent
}