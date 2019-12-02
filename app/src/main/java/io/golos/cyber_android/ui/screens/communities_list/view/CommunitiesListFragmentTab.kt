package io.golos.cyber_android.ui.screens.communities_list.view

import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_list_fragment.CommunitiesListFragmentTabComponent

/**
 * Communities list for tabs on [MainActivity]
 */
class CommunitiesListFragmentTab : CommunitiesListFragment() {
    companion object {
        fun newInstance() = CommunitiesListFragmentTab()
    }

    override fun inject() = App.injections.get<CommunitiesListFragmentTabComponent>(false).inject(this)

    override fun releaseInjection() {
        App.injections.release<CommunitiesListFragmentTabComponent>()
    }
}