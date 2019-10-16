package io.golos.cyber_android.ui.shared_fragments.post.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.*
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.comments.CommentsAdapter
import io.golos.cyber_android.ui.shared_fragments.post.view.view_holders.*
import io.golos.cyber_android.ui.shared_fragments.post.view.view_holders.post_text.PostTextViewHolder
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelItemsClickProcessor
import io.golos.domain.interactors.model.*
import kotlinx.android.synthetic.main.footer_post_card.view.*

class PostPageAdapter(
    private val lifecycleOwner: LifecycleOwner,
    commentListener: CommentsAdapter.Listener,
    val listener: Listener,
    private val clicksProcessor: PostPageViewModelItemsClickProcessor
) : CommentsAdapter(emptyList(), commentListener) {

    interface Listener {
        fun onPostUpvote(postModel: PostModel)
        fun onPostDownvote(postModel: PostModel)
    }

    private lateinit var recyclerView: RecyclerView

    override var isLoading = true
        set(value) {
            field = value
            if (itemCount > 0)
                notifyItemChanged(getLoadingViewHolderPosition())
        }

    var postModel: PostModel? = null
        set(value) {
            field = value
            if (itemCount > 0)
                //we cant just use notifyItemRangeChanged(0, getItemsOffset()) since updating of the post
                //will lead to changes in headers count, hence all of the items should be updated,
                //including comments
                notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            PostPageViewType.POST_CONTROLS_TYPE -> PostControlsViewHolder(parent)
            PostPageViewType.COMMENT_TITLE_TYPE -> CommentsTitleViewHolder(parent)
            PostPageViewType.LOADING_TYPE -> LoadingViewHolder(parent)
            PostPageViewType.CONTENT_TEXT_TYPE -> PostTextViewHolder(parent, clicksProcessor)        // Post and title

            PostPageViewType.COMMENT_TYPE -> super.onCreateViewHolder(parent, viewType)

            else -> throw RuntimeException("Unsupported view type")
        }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            PostPageViewType.POST_CONTROLS_TYPE ->
                postModel?.let { (holder as PostControlsViewHolder).bind(holder.itemView, it, listener) }

            PostPageViewType.COMMENT_TITLE_TYPE ->
                (holder as CommentsTitleViewHolder).bind(holder.itemView, postModel?.comments?.count ?: 0)

            PostPageViewType.LOADING_TYPE ->
                (holder as LoadingViewHolder).bind(holder.itemView, isLoading)

            PostPageViewType.CONTENT_TEXT_TYPE ->
                postModel?.let { (holder as PostTextViewHolder).bind(holder.itemView, it.content.body.postBlock) }

//            PostPageViewType.COMMENT_TYPE -> super.onBindViewHolder(holder, position - getItemsOffset())
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)

        when (holder) {
            is PostTextViewHolder -> holder.cleanUp(holder.itemView)
            else -> {}
        }
    }

//    override fun getItemViewType(position: Int): Int = CONTENT_TEXT_TYPE

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            0 -> PostPageViewType.CONTENT_TEXT_TYPE
            1 -> PostPageViewType.POST_CONTROLS_TYPE
            2 -> PostPageViewType.COMMENT_TITLE_TYPE
            else -> PostPageViewType.COMMENT_TYPE

        }

//        if (postModel != null) {
//            with(postModel!!.content.body.postBlock) {
//                if (position in getPostContentPositionStart() until (/*postModel!!.content.body.full.size*/ 1 + getPostContentPositionStart())) {
//                    if (content.isNotEmpty() || attachments?.content?.isNotEmpty() == true)
//                        return CONTENT_TEXT_TYPE /*when (postModel!!.content.body.full[adapterPositionToContentRowPosition(position)]) {
//                        is TextRowModel -> CONTENT_TEXT_TYPE
//                        is ImageRowModel -> CONTENT_IMAGE_TYPE
//                    }*/
//                }
//            }
//        }
//        return when (position) {
//            getPostControlsPosition() -> POST_CONTROLS_TYPE
//            getCommentsTitlePosition() -> COMMENT_TITLE_TYPE
//            getLoadingViewHolderPosition() -> LOADING_TYPE
//            else -> COMMENT_TYPE
//        }
    }

    /**
     * Content starts after embeds and one [PostControlsViewHolder] (with controls like upvote, downvote etc)
     */
    private fun getPostContentPositionStart() = getEmbedsCount() + 1

    private fun adapterPositionToContentRowPosition(position: Int) = position - 1 - getEmbedsCount()


    fun scrollToComment(commentModel: CommentModel, recyclerView: RecyclerView) {
        if (values.contains(commentModel))
            (recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                values.indexOf(commentModel) + getItemsOffset(),
                0
            )
    }

    /**
     * Return headers count to offset real comments to pass them to the parent [CommentsAdapter] via [updateCallback]
     */
    private fun getItemsOffset() = getHeadersCount()

    /**
     * Post controls goes right after all embeds
     */
    private fun getPostControlsPosition() = getEmbedsCount()

    /**
     * Returns position of comments title ("N comments") - which is last header
     */
    fun getCommentsTitlePosition() = getHeadersCount() - 1

    /**
     * Calculates headers count. Headers count =
     * (count of the [ContentRowModel] in [ContentBodyModel]) +
     * (count of the [EmbedModel] in [ContentBodyModel]) +
     * (1 for view with controls like upvote, downvote etc) +
     * (1 for comments title)
     */
    private fun getHeadersCount() = 1 + getPostContentPositionStart() + 1

//    private fun getHeadersCount() =
//        (postModel?.content?.body?.full?.size ?: 0) + getPostContentPositionStart() + 1


    /**
     * Returns [getItemCount] of superclass but also adds [getItemsOffset] (which is amount of the headers in adapter)
     * and 1 (which is loading view at the bottom)
     */
    override fun getItemCount() = getItemsOffset() + 1

    /**
     * Returns position of loading view holder
     */
    private fun getLoadingViewHolderPosition() = itemCount - 1

    /**
     * Use only first embed if any
     */
    private fun getEmbedsCount() = 0

    private val adapterCallback = AdapterListUpdateCallback(this)
    private val updateCallback = object : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapterCallback.onChanged(position + getItemsOffset(), count, payload)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapterCallback.onMoved(fromPosition + getItemsOffset(), toPosition + getItemsOffset())
        }

        override fun onInserted(position: Int, count: Int) {
            adapterCallback.onInserted(position + getItemsOffset(), count)
        }

        override fun onRemoved(position: Int, count: Int) {
            adapterCallback.onRemoved(position + getItemsOffset(), count)
        }
    }

    override fun dispatchUpdates(diff: DiffUtil.DiffResult) {
        diff.dispatchUpdatesTo(updateCallback)
    }
}