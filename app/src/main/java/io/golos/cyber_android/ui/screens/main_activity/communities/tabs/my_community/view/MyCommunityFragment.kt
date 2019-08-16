package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.my_community.view

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.my_community_fragment.MyCommunityFragmentComponent
import io.golos.cyber_android.databinding.FragmentMyCommunitiesBinding
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.view.CommunitiesTabFragmentBase
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.viewModel.CommunityViewModel
import kotlinx.android.synthetic.main.fragment_my_communities.root as rootView
import kotlinx.android.synthetic.main.fragment_my_communities.searchResultList as searchResultListView
import kotlinx.android.synthetic.main.fragment_my_communities.mainList as mainListView

class MyCommunityFragment : CommunitiesTabFragmentBase<FragmentMyCommunitiesBinding>() {

    companion object {
        fun newInstance() = MyCommunityFragment()
    }

    override val root: ConstraintLayout
        get() = rootView

    override val searchResultList: RecyclerView
        get() = searchResultListView

    override val mainList: RecyclerView
        get() = mainListView

    override fun provideLayout(): Int = R.layout.fragment_my_communities

    override fun inject() = App.injections.get<MyCommunityFragmentComponent>().inject(this)

    override fun releaseInjection() {
        App.injections.release<MyCommunityFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentMyCommunitiesBinding, viewModel: CommunityViewModel) {
        binding.viewModel = viewModel
    }
}