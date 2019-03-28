package io.golos.cyber_android.ui.screens.post

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.comments.CommentsAdapter
import io.golos.cyber_android.ui.common.posts.AbstractFeedFragment
import io.golos.cyber_android.utils.DateUtils
import io.golos.domain.entities.CommentEntity
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.model.CommentFeedUpdateRequest
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.android.synthetic.main.header_post_card.*

/**
 * Fragment for single [PostModel] presentation
 */
class PostFragment :
    AbstractFeedFragment<CommentFeedUpdateRequest, CommentEntity, CommentModel, PostWithCommentsViewModel>() {
    override val feedList: RecyclerView
        get() = postView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()

        viewModel.postLiveData.observe(this, Observer {
            bindPostModel(it)
        })

        viewModel.loadingStatusLiveData.observe(this, Observer {
            showLoading()
        })

        postMenu.setColorFilter(Color.BLACK)
        back.setOnClickListener { activity?.finish() }
    }

    private fun showLoading() {

    }

    private fun bindPostModel(postModel: PostModel) {
        (feedList.adapter as PostAdapter).postModel = postModel
        bindToolbar(postModel)
    }

    private fun bindToolbar(postModel: PostModel) {
        //postAvatar
        //postMenu

        postAuthorName.text = postModel.community.name
        postAuthor.text = String.format(
            resources.getString(R.string.post_time_and_author_format),
            DateUtils.createTimeLabel(
                postModel.meta.time.time,
                postModel.meta.elapsedFormCreation.elapsedMinutes,
                postModel.meta.elapsedFormCreation.elapsedHours,
                postModel.meta.elapsedFormCreation.elapsedDays,
                requireContext()
            ),
            postModel.author.username
        )
    }

    override fun setupEventsProvider() {
    }

    override fun setupFeedAdapter() {
        feedList.adapter = PostAdapter(object : CommentsAdapter.Listener {
            override fun onUpvoteClick(comment: CommentModel) {
            }

            override fun onDownvoteClick(comment: CommentModel) {
            }

            override fun onReplyClick(comment: CommentModel) {
            }

        }, object : PostAdapter.Listener {
            override fun onPostUpvote(postModel: PostModel) {
                viewModel.onPostUpote()
            }

            override fun onPostDownvote(postModel: PostModel) {
                viewModel.onPostDownvote()
            }

        })
    }

    override fun onNewData() {
    }

    override fun setupWidgetsLiveData() {
    }

    override fun setupViewModel() {
        val id = DiscussionIdModel(
            arguments!!.getString(Tags.USER_ID)!!,
            arguments!!.getString(Tags.PERM_LINK)!!,
            arguments!!.getLong(Tags.REF_BLOCK_NUM)
        )
        viewModel = ViewModelProviders.of(
            this,
            requireActivity()
                .serviceLocator
                .getPostWithCommentsViewModelFactory(id)
        ).get(PostWithCommentsViewModel::class.java)
    }


}