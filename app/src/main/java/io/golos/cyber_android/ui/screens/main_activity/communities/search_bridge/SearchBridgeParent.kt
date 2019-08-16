package io.golos.cyber_android.ui.screens.main_activity.communities.search_bridge

interface SearchBridgeParent {
    fun attachParent(parent: ParentSearchFragment)

    fun attachChild(position: Int, child: ChildSearchFragment)

    fun getChild(position: Int): ChildSearchFragment
}