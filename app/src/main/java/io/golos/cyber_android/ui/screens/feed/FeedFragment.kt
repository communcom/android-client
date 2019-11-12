package io.golos.cyber_android.ui.screens.feed

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.feed.FeedFragmentComponent
import io.golos.cyber_android.databinding.FragmentFeedBinding
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.extensions.reduceDragSensitivity
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowPostFiltersCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.common.utils.TabLayoutMediator
import io.golos.cyber_android.ui.screens.myfeed.view.MyFeedFragment
import io.golos.cyber_android.ui.screens.post_filters.PostFilters
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersBottomSheetDialog
import io.golos.cyber_android.ui.dto.GetPostsByUserIdConfiguration
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.ldralighieri.corbind.view.clicks


const val SORT_REQUEST_CODE = 100
const val FEED_REQUEST_CODE = 101
const val EDITOR_WIDGET_PHOTO_REQUEST_CODE = 102
const val REQUEST_POST_CREATION = 205


class FeedFragment : FragmentBaseMVVM<FragmentFeedBinding, FeedViewModel>(),
    FeedPageLiveDataProvider {

    override fun releaseInjection() {
        App.injections.release<FeedFragmentComponent>()
    }

    override fun provideViewModelType(): Class<FeedViewModel> = FeedViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_feed

    override fun inject() {
        App.injections
            .get<FeedFragmentComponent>()
            .inject(this)
    }

    override fun linkViewModel(binding: FragmentFeedBinding, viewModel: FeedViewModel) {
        binding.viewModel = viewModel
    }

    private enum class FeedTabs(@StringRes val title: Int, val index: Int) {
        MY_FEED(R.string.my_feed, 0), TRENDING(R.string.trending, 1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupTabLayout()
        launch {
            ivFilters
                .clicks()
                .collect {
                    viewModel.onFiltersCLicked()
                }
        }
        attachPostFiltersListener()
    }

    private fun setupViewPager() {
        feedPager.adapter = object : FragmentStateAdapter(requireFragmentManager(), this.lifecycle) {
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    FeedTabs.TRENDING.index -> TrendingFeedFragment.newInstance(
                        arguments?.getString(Tags.COMMUNITY_NAME)!!,
                        arguments?.getString(Tags.USER_ID)!!
                    ).apply {
                        setTargetFragment(this@FeedFragment, FEED_REQUEST_CODE)
                    }
                    FeedTabs.MY_FEED.index -> MyFeedFragment.newInstance(
                        GetPostsByUserIdConfiguration(
                            arguments?.getString(Tags.USER_ID)!!
                        )
                    )
                    else -> throw RuntimeException("Unsupported tab")
                }
            }

            override fun getItemCount() = FeedTabs.values().size
        }
        feedPager.reduceDragSensitivity()
    }

    private fun setupTabLayout() {
        TabLayoutMediator(tabLayout, feedPager) { tab, position ->
            tab.customView =
                LayoutInflater.from(requireContext()).inflate(R.layout.layout_feed_tab, null) as TextView
            tab.customView?.findViewById<TextView>(R.id.tvTabText)
                ?.setText(FeedTabs.values()[position].title)
        }.attach()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabReselected(tab: TabLayout.Tab) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                setupTab(tab)
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                setupTab(tab)
            }

        })
        setupTabsText()
    }

    private fun setupTabsText() {
        for (tabPage in FeedTabs.values()) {
            tabLayout.getTabAt(tabPage.index)?.let {
                setupTab(it)
            }
        }
    }

    private fun setupTab(tab: TabLayout.Tab, forceSelected: Boolean = false) {
        val tvTabText = tab.customView?.findViewById<TextView>(R.id.tvTabText)
        if (tab.isSelected || forceSelected) {
            tvTabText?.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.feed_tab_text_selected))
        } else {
            tvTabText?.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.feed_tab_text_normal))
        }
    }

    override fun provideEventsLiveData() = viewModel.getEventsLiveData

    override fun processViewCommand(command: ViewCommand) {
        super.processViewCommand(command)
        if (command is ShowPostFiltersCommand) {
            val tag = PostFiltersBottomSheetDialog::class.java.name
            if (childFragmentManager.findFragmentByTag(tag) == null) {
                val postFiltersBottomSheetDialog = PostFiltersBottomSheetDialog()
                postFiltersBottomSheetDialog.show(childFragmentManager, tag)
                postFiltersBottomSheetDialog.setFiltersApplyListener(PostFiltersListener)
            }
        }
    }

    private fun attachPostFiltersListener(){
        val tag = PostFiltersBottomSheetDialog::class.java.name
        childFragmentManager.findFragmentByTag(tag)?.let {
            (it as PostFiltersBottomSheetDialog).setFiltersApplyListener(PostFiltersListener)
        }
    }

    private object PostFiltersListener: PostFiltersBottomSheetDialog.FilterChangeListener{
        override fun onFiltersChanged(filters: PostFilters) {

        }

    }

    private fun removePostFiltersListener(){
        val tag = PostFiltersBottomSheetDialog::class.java.name
        childFragmentManager.findFragmentByTag(tag)?.let {
            (it as PostFiltersBottomSheetDialog).setFiltersApplyListener(null)
        }
    }

    override fun onDestroyView() {
        removePostFiltersListener()
        super.onDestroyView()
    }

    companion object {
        fun newInstance(communityName: String, userId: String) =
            FeedFragment().apply {
                arguments = Bundle().apply {
                    putString(Tags.COMMUNITY_NAME, communityName)
                    putString(Tags.USER_ID, userId)
                }
            }
    }
}
