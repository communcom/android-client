package io.golos.cyber_android.ui.screens.ftue.view

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentFtueBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.screens.community_page.child_pages.leads_list.view.LeadsListFragment
import io.golos.cyber_android.ui.screens.community_page.dto.CommunityPage
import io.golos.cyber_android.ui.screens.community_page.view.CommunityPageFragment
import io.golos.cyber_android.ui.screens.community_page_about.CommunityPageAboutFragment
import io.golos.cyber_android.ui.screens.community_page_rules.CommunityPageRulesFragment
import io.golos.cyber_android.ui.screens.followers.FollowersFragment
import io.golos.cyber_android.ui.screens.ftue.di.FtueFragmentComponent
import io.golos.cyber_android.ui.screens.ftue.viewmodel.FtueViewModel
import io.golos.cyber_android.ui.screens.ftuesearchcommunity.view.FtueSearchCommunityFragment
import kotlinx.android.synthetic.main.fragment_community_page.*
import kotlinx.android.synthetic.main.fragment_ftue.*

class FtueFragment : FragmentBaseMVVM<FragmentFtueBinding, FtueViewModel>() {

    private var fragmentPagesList: List<Fragment> = ArrayList()

    private val ftuePageAdapter by lazy {

        object : FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

            override fun getPageTitle(position: Int): CharSequence? {
                return ""
            }

            override fun getItem(position: Int): Fragment = fragmentPagesList[position]

            override fun getCount(): Int = fragmentPagesList.size

        }
    }

    override fun provideViewModelType(): Class<FtueViewModel> = FtueViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_ftue

    override fun inject() = App.injections.get<FtueFragmentComponent>()
        .inject(this)

    override fun releaseInjection() {
        App.injections.release<FtueFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentFtueBinding, viewModel: FtueViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCommunUrl()
        setupViewPage()
    }

    private fun setupViewPage(){
        this.fragmentPagesList = createPagesList()
        viewPager.adapter = ftuePageAdapter
    }

    private fun createPagesList(): List<Fragment>{
        val fragmentPagesList = mutableListOf<Fragment>()
        fragmentPagesList.add(FtueSearchCommunityFragment.newInstance())
        return fragmentPagesList
    }

    private fun setupCommunUrl(){
        val commun = SpannableStringBuilder(getString(R.string.commun))
        val slash = SpannableStringBuilder(" /")
        val blueColor = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.blue))
        val blackColor = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.black))
        commun.setSpan(
            blackColor,
            0,
            commun.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        slash.setSpan(
            blueColor,
            0,
            slash.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        tvCommunUrl.text = SpannableStringBuilder(TextUtils.concat(commun, slash))
    }
}