package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.view

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.discover_fragment.DiscoverFragmentComponent
import io.golos.cyber_android.databinding.FragmentDiscoverBinding
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.view.CommunitiesTabFragmentBase
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.viewModel.CommunityViewModel
import io.golos.domain.AppResourcesProvider
import kotlinx.android.synthetic.main.fragment_discover.*
import kotlinx.android.synthetic.main.fragment_discover.root as rootView
import kotlinx.android.synthetic.main.fragment_discover.searchResultList as searchResultListView
import kotlinx.android.synthetic.main.fragment_discover.mainList as mainListView

import javax.inject.Inject

class DiscoverFragment : CommunitiesTabFragmentBase<FragmentDiscoverBinding>() {
    companion object {
        fun newInstance() = DiscoverFragment()
    }

    override val root: ConstraintLayout
        get() = rootView

    override val searchResultList: RecyclerView
        get() = searchResultListView

    override val mainList: RecyclerView
        get() = mainListView

    @Inject
    internal lateinit var appResources: AppResourcesProvider

    override fun provideLayout(): Int = R.layout.fragment_discover

    override fun inject() = App.injections.get<DiscoverFragmentComponent>().inject(this)

    override fun releaseInjection() {
        App.injections.release<DiscoverFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentDiscoverBinding, viewModel: CommunityViewModel) {
        binding.viewModel = viewModel
    }

    override fun updateSearchResultVisibility(isVisible: Boolean) {
        super.updateSearchResultVisibility(isVisible)

        val labelLayoutParams = recomendedText.layoutParams as ConstraintLayout.LayoutParams
        val labelTopMargin = if(isVisible) appResources.getDimens(R.dimen.margin_vertical_default_16dp).toInt() else 0
        labelLayoutParams.setMargins(0, labelTopMargin, 0, 0)
        recomendedText.layoutParams = labelLayoutParams
    }
}