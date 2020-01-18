package io.golos.cyber_android.ui.screens.feed

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import com.google.android.material.tabs.TabLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentFeedBinding
import io.golos.cyber_android.ui.screens.feed.di.FeedFragmentComponent
import io.golos.cyber_android.ui.screens.feed_my.view.MyFeedFragment
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersDialog
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.cyber_android.ui.shared.Tags
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowPostFiltersCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.ldralighieri.corbind.view.clicks

class FeedFragment : FragmentBaseMVVM<FragmentFeedBinding, FeedViewModel>(),
    FeedPageLiveDataProvider {

    override fun provideViewModelType(): Class<FeedViewModel> = FeedViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_feed

    override fun inject(key: String) = App.injections.get<FeedFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<FeedFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentFeedBinding, viewModel: FeedViewModel) {
        binding.viewModel = viewModel
    }

    private enum class FeedTabs(@StringRes val title: Int, val index: Int) {
        MY_FEED(R.string.my_feed, 0), TRENDING(R.string.trending, 1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragmentContainer()
        setupTabLayout()
        launch {
            ivFilters
                .clicks()
                .collect {
                    viewModel.onFiltersCLicked()
                }
        }
    }

    private fun setupFragmentContainer() {
        if (childFragmentManager.findFragmentByTag(tag) == null) {
            val beginTransaction = childFragmentManager.beginTransaction()
            beginTransaction
                .addToBackStack(null)
                .add(R.id.feedContainer, MyFeedFragment.newInstance(), tag)
                .commit()
        }
    }

    private fun setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().apply {
            customView = getCustomView(FeedTabs.MY_FEED)
            tag = FeedTabs.MY_FEED
        })
        tabLayout.addTab(tabLayout.newTab().apply {
            customView = getCustomView(FeedTabs.TRENDING)
            tag = FeedTabs.TRENDING
        })
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabReselected(tab: TabLayout.Tab) {}

            override fun onTabUnselected(tab: TabLayout.Tab) {
                setupTab(tab)
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                setupTab(tab)
                val currentFeed = when (tab.tag as FeedTabs) {
                    FeedTabs.MY_FEED -> PostFiltersHolder.CurrentOpenTypeFeed.MY_FEED
                    FeedTabs.TRENDING -> PostFiltersHolder.CurrentOpenTypeFeed.TRENDING
                }
                viewModel.onChangeTabFilter(currentFeed)
            }

        })
        setupTabsText()
    }

    private fun getCustomView(tab: FeedTabs): View {
        val view = LayoutInflater.from(requireContext()).inflate(
            R.layout.layout_feed_tab,
            null
        ) as TextView
        view.setText(tab.title)
        return view
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
            val tag = PostFiltersDialog::class.java.name
            if (childFragmentManager.findFragmentByTag(tag) == null) {
                val postFiltersBottomSheetDialog =
                    PostFiltersDialog.newInstance(true)
                postFiltersBottomSheetDialog.show(childFragmentManager, tag)
            }
        }
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
