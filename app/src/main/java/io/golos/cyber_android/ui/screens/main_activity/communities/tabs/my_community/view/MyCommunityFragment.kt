package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.my_community.view

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.my_community_fragment.MyCommunityFragmentComponent
import io.golos.cyber_android.databinding.FragmentCommunitiesMyBinding
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.view.CommunitiesTabFragmentBase
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.viewModel.CommunityViewModel
import kotlinx.android.synthetic.main.fragment_communities_my.root as rootView
import kotlinx.android.synthetic.main.fragment_communities_my.searchResultList as searchResultListView
import kotlinx.android.synthetic.main.fragment_communities_my.mainList as mainListView

class MyCommunityFragment : CommunitiesTabFragmentBase<FragmentCommunitiesMyBinding>() {

    companion object {
        fun newInstance() = MyCommunityFragment()
    }

    override val root: ConstraintLayout
        get() = rootView

    override val searchResultList: RecyclerView
        get() = searchResultListView

    override val mainList: RecyclerView
        get() = mainListView

    override fun layoutResId(): Int = R.layout.fragment_communities_my

    override fun inject() = App.injections.get<MyCommunityFragmentComponent>().inject(this)

    override fun releaseInjection() {
        App.injections.release<MyCommunityFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentCommunitiesMyBinding, viewModel: CommunityViewModel) {
        binding.viewModel = viewModel
    }
}