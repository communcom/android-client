package io.golos.cyber_android.ui.screens.notifications.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentNotificationsBinding
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.feed_my.di.MyFeedFragmentComponent
import io.golos.cyber_android.ui.screens.notifications.di.NotificationsFragmentComponent
import io.golos.cyber_android.ui.screens.notifications.view.list.NotificationsAdapter
import io.golos.cyber_android.ui.screens.notifications.view.list.items.BaseNotificationItem
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationEmptyStubItem
import io.golos.cyber_android.ui.screens.notifications.view.list.items.NotificationHeaderDateItem
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModel
import io.golos.cyber_android.ui.screens.post_view.view.PostPageFragment
import io.golos.cyber_android.ui.screens.profile.view.ProfileExternalUserFragment
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToPostCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToUserProfileCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.shared.utils.*
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.utils.IdUtil
import kotlinx.android.synthetic.main.fragment_notifications.*

class NotificationsFragment : FragmentBaseMVVM<FragmentNotificationsBinding, NotificationsViewModel>() {

    override fun provideViewModelType(): Class<NotificationsViewModel> = NotificationsViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_notifications

    override fun inject(key: String) = App.injections.get<NotificationsFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<MyFeedFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentNotificationsBinding, viewModel: NotificationsViewModel) {
        binding.viewModel = viewModel
    }

    companion object {
        fun newInstance() = NotificationsFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNotificationsList()
        observeViewModel()
    }

    private fun setupNotificationsList() {
        val communityAdapter = NotificationsAdapter(viewModel, PAGINATION_PAGE_SIZE)
        val lManager = LinearLayoutManager(requireContext())
        rvNotifications.layoutManager = lManager
        rvNotifications.adapter = communityAdapter
    }

    private fun observeViewModel() {
        viewModel.notificationsListState.observe(viewLifecycleOwner, Observer { state ->
            val adapter = rvNotifications.adapter as NotificationsAdapter
            when (state) {
                is Paginator.State.Data<*> -> {
                    adapter.removeProgress()
                    adapter.removeRetry()
                    val items = (state.data as MutableList<VersionedListItem>)
                    val itemsWithDates = addDateSections(items)
                    adapter.update(itemsWithDates)
                    btnRetry.visibility = View.INVISIBLE
                    emptyProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.FullData<*> -> {
                    adapter.removeProgress()
                    adapter.removeRetry()
                    val items = (state.data as MutableList<VersionedListItem>)
                    val itemsWithDates = addDateSections(items)
                    adapter.update(itemsWithDates)
                    btnRetry.visibility = View.INVISIBLE
                    emptyProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.PageError<*> -> {
                    adapter.removeProgress()
                    adapter.addRetry()
                    rvNotifications.scrollToPosition(adapter.itemCount - 1)
                }
                is Paginator.State.NewPageProgress<*> -> {
                    adapter.removeRetry()
                    adapter.addProgress()
                    rvNotifications.scrollToPosition(adapter.itemCount - 1)
                }
                is Paginator.State.EmptyProgress -> {
                    adapter.removeProgress()
                    adapter.removeRetry()
                    btnRetry.visibility = View.INVISIBLE
                    emptyProgressLoading.visibility = View.VISIBLE
                }
                is Paginator.State.Empty -> {
                    adapter.update(mutableListOf(NotificationEmptyStubItem(0, IdUtil.generateLongId())))
                    adapter.removeProgress()
                    adapter.removeRetry()
                    btnRetry.visibility = View.INVISIBLE
                    emptyProgressLoading.visibility = View.INVISIBLE
                }
                is Paginator.State.EmptyError -> {
                    adapter.removeProgress()
                    adapter.removeRetry()
                    btnRetry.visibility = View.VISIBLE
                    emptyProgressLoading.visibility = View.INVISIBLE
                }
            }
        })
        viewModel.newNotificationsCount.observe(viewLifecycleOwner, Observer {
            if(it == 0){
                tvUnreadCountLabel.visibility = View.INVISIBLE
            } else{
                tvUnreadCountLabel.visibility = View.VISIBLE
                tvUnreadCountLabel.text = getString(R.string.new_notifications_label, it)
            }
        })
    }

    override fun processViewCommand(command: ViewCommand) {
        super.processViewCommand(command)
        when(command){
            is NavigateToUserProfileCommand -> openUserProfile(command.userId)
            is NavigateToPostCommand -> openPost(command.discussionIdModel, command.contentId)
        }
    }

    private fun openUserProfile(userId: UserIdDomain) {
        getDashboardFragment(this)?.showFragment(
            ProfileExternalUserFragment.newInstance(userId)
        )
    }

    private fun openPost(
        discussionIdModel: DiscussionIdModel,
        contentId: ContentId
    ) {
        getDashboardFragment(this)?.showFragment(
            PostPageFragment.newInstance(
                PostPageFragment.Args(
                    discussionIdModel,
                    contentId
                )
            ),
            tagFragment = contentId.permlink
        )
    }

    private fun addDateSections(items: MutableList<VersionedListItem>): MutableList<VersionedListItem>{
        val itemsWithSections = mutableListOf<VersionedListItem>()
        val beginToday = beginToday()
        val yesterday = beginYesterday()
        val beginWeek = beginWeek()
        val beginMonth = beginMonth()

        val currentDayNotifications = mutableListOf<VersionedListItem>()
        items.filterTo(currentDayNotifications) {
            val createTime = (it as BaseNotificationItem).createTime
            beginToday.before(createTime)
        }
        val yesterdayNotifications  = mutableListOf<VersionedListItem>()
        items.filterTo(yesterdayNotifications) {
            val createTime = (it as BaseNotificationItem).createTime
            yesterday.before(createTime) && beginToday.after(createTime)
        }

        val weekNotifications = mutableListOf<VersionedListItem>()
        items.filterTo(weekNotifications) {
            val createTime = (it as BaseNotificationItem).createTime
            beginWeek.before(createTime) && yesterday.after(createTime)
        }
        val monthNotifications = mutableListOf<VersionedListItem>()
        items.filterTo(monthNotifications) {
            val createTime = (it as BaseNotificationItem).createTime
            beginMonth.before(createTime) && beginWeek.after(createTime)
        }
        val otherNotifications = mutableListOf<VersionedListItem>()
        items.filterTo(otherNotifications) {
            val createTime = (it as BaseNotificationItem).createTime
            beginMonth.after(createTime)
        }


        if(currentDayNotifications.isNotEmpty()){
            itemsWithSections.add(NotificationHeaderDateItem(0, IdUtil.generateLongId(), getString(R.string.today)))
            itemsWithSections.addAll(currentDayNotifications)
        }
        if(yesterdayNotifications.isNotEmpty()){
            itemsWithSections.add(NotificationHeaderDateItem(0, IdUtil.generateLongId(), getString(R.string.yesterday)))
            itemsWithSections.addAll(yesterdayNotifications)
        }
        if(weekNotifications.isNotEmpty()){
            itemsWithSections.add(NotificationHeaderDateItem(0, IdUtil.generateLongId(), getString(R.string.this_week)))
            itemsWithSections.addAll(weekNotifications)
        }
        if(monthNotifications.isNotEmpty()){
            itemsWithSections.add(NotificationHeaderDateItem(0, IdUtil.generateLongId(), getString(R.string.this_month)))
            itemsWithSections.addAll(monthNotifications)
        }
        if(otherNotifications.isNotEmpty()){
            itemsWithSections.add(NotificationHeaderDateItem(0, IdUtil.generateLongId(), getString(R.string.other)))
            itemsWithSections.addAll(otherNotifications)
        }
        return itemsWithSections
    }
}
