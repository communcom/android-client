package io.golos.cyber_android.ui.screens.main_activity.notifications

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.shared_fragments.post.PostActivity
import io.golos.cyber_android.ui.shared_fragments.post.PostPageFragment
import io.golos.cyber_android.ui.screens.profile.ProfileActivity
import io.golos.cyber_android.utils.PaginationScrollListener
import io.golos.domain.requestmodel.*
import kotlinx.android.synthetic.main.fragment_notifications.*

/**
 * [Fragment] that represents Notification Page of the main screen. Contains a list of events, supports infinite
 * scrolling and swipe-to-refresh
 */
class NotificationsFragment : Fragment() {

    private lateinit var viewModel: NotificationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()

        swipeRefresh.setOnRefreshListener {
            viewModel.requestRefresh()
        }

        notificationsList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        notificationsList.adapter = NotificationsAdapter { event ->
            when (event) {
                is VoteEventModel -> showPost(event.post)
                is FlagEventModel -> showPost(event.post)
                is TransferEventModel -> showActor(event.actor)
                is SubscribeEventModel -> showActor(event.actor)
                is UnSubscribeEventModel -> showActor(event.actor)
                is ReplyEventModel -> showPost(event.parentPost)
                is MentionEventModel -> showPost(event.parentPost)
                is RepostEventModel -> showPost(event.post)
                is AwardEventModel -> {
                }
                is CuratorAwardEventModel -> showPost(event.post)
                is WitnessVoteEventModel -> showActor(event.actor)
                is WitnessCancelVoteEventModel -> showActor(event.actor)
            }
        }

        val paginationScrollListener = object :
            PaginationScrollListener(notificationsList.layoutManager as LinearLayoutManager, NotificationsViewModel.PAGE_SIZE) {
            override fun loadMoreItems() {
                viewModel.loadMore()
            }
        }
        notificationsList.addOnScrollListener(paginationScrollListener)

        observeViewModel(paginationScrollListener)

    }

    private fun showActor(actor: EventActorModel) {
        startActivity(ProfileActivity.getIntent(requireContext(), actor.id.name))
    }

    private fun showPost(eventPostModel: EventPostModel?) {
        if (eventPostModel?.contentId != null) {
            startActivity(PostActivity.getIntent(requireContext(), PostPageFragment.Args(eventPostModel.contentId)))
        }
    }

    private fun observeViewModel(paginationScrollListener: PaginationScrollListener) {
        @Suppress("UNCHECKED_CAST")
        viewModel.feedLiveData.observe(this, Observer {
            if (it.isNotEmpty()) {
                (notificationsList.adapter as NotificationsAdapter).submit(it)
                notificationsList.visibility = View.VISIBLE
                empty.visibility = View.GONE
            } else {
                notificationsList.visibility = View.GONE
                empty.visibility = View.VISIBLE
            }
            swipeRefresh.isRefreshing = false
        })

        viewModel.loadingStatusLiveData.observe(this, Observer { isLoading ->
            paginationScrollListener.isLoading = isLoading
        })

        viewModel.readinessLiveData.observe(this, Observer { isReady ->
            if (isReady) {
                progress.visibility = View.GONE
                swipeRefresh.visibility = View.VISIBLE
            } else {
                progress.visibility = View.VISIBLE
                swipeRefresh.visibility = View.GONE
            }
        })

        @Suppress("UNCHECKED_CAST")
        viewModel.lastPageLiveData.observe(this, Observer { isLastPage ->
            paginationScrollListener.isLastPage = isLastPage
            (notificationsList.adapter as NotificationsAdapter).isLoading = !isLastPage
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            requireActivity()
                .serviceLocator
                .getDefaultViewModelFactory()
        ).get(NotificationsViewModel::class.java)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            val savedRecyclerLayoutState = savedInstanceState.getParcelable(javaClass.name) as Parcelable
            notificationsList.layoutManager?.onRestoreInstanceState(savedRecyclerLayoutState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (notificationsList.layoutManager != null)
            outState.putParcelable(javaClass.name, notificationsList.layoutManager?.onSaveInstanceState())
    }

    companion object {
        fun newInstance() = NotificationsFragment()
    }

}
