package io.golos.cyber_android.ui.screens.community_page

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page.CommunityPageFragmentComponent
import io.golos.cyber_android.databinding.FragmentCommunityPageBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.widgets.TabLineDrawable
import io.golos.cyber_android.ui.screens.followers.FollowersFragment
import kotlinx.android.synthetic.main.fragment_community_page.*

class CommunityPageFragment : FragmentBaseMVVM<FragmentCommunityPageBinding, CommunityPageModel, CommunityPageViewModel>() {

    private var fragmentPagesList: List<Fragment> = ArrayList()

    private val tabTitles by lazy {
        requireContext().resources.getStringArray(R.array.community_page_tab_titles)
    }

    private val communityPagerTabAdapter by lazy {

        object : FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

            override fun getPageTitle(position: Int): CharSequence? {
                return tabTitles.getOrNull(position)
            }

            override fun getItem(position: Int): Fragment = fragmentPagesList[position]

            override fun getCount(): Int = fragmentPagesList.size

        }
    }

    override fun provideViewModelType(): Class<CommunityPageViewModel> = CommunityPageViewModel::class.java

    override fun provideLayout(): Int = R.layout.fragment_community_page

    override fun inject() = App.injections
        .get<CommunityPageFragmentComponent>()
        .inject(this)

    override fun linkViewModel(binding: FragmentCommunityPageBinding, viewModel: CommunityPageViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTablayout()
        initViewPager()
    }

    private fun initTablayout() {
        tabLayout.apply {
            setupWithViewPager(vpContent)
            setSelectedTabIndicator(TabLineDrawable(requireContext()))
            setSelectedTabIndicatorColor(ContextCompat.getColor(requireContext(), R.color.blue))
        }
    }

    private fun initViewPager() {
        fragmentPagesList = createPageFragmentsList()
        vpContent.adapter = communityPagerTabAdapter
    }

    private fun createPageFragmentsList(): MutableList<Fragment> {
        val fragmentPagesList = ArrayList<Fragment>()
        fragmentPagesList.add(FollowersFragment.newInstance())
        fragmentPagesList.add(FollowersFragment.newInstance())
        fragmentPagesList.add(FollowersFragment.newInstance())
        fragmentPagesList.add(FollowersFragment.newInstance())
        return fragmentPagesList
    }

    companion object {

        fun newInstance(): CommunityPageFragment = CommunityPageFragment()
    }
}