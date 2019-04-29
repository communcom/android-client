package io.golos.cyber_android.ui.screens.post.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.*
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.comments.CommentsAdapter
import io.golos.domain.interactors.model.*
import kotlinx.android.synthetic.main.footer_post_card.view.*

private const val POST_CONTROLS_TYPE = 0
private const val COMMENT_TYPE = 1
private const val LOADING_TYPE = 2


private const val CONTENT_TEXT_TYPE = 3
private const val CONTENT_IMAGE_TYPE = 4
private const val CONTENT_EMBED_TYPE = 5

private const val COMMENT_TITLE_TYPE = 6

/**
 * Position of the post header
 */

class PostPageAdapter(
    private val lifecycleOwner: LifecycleOwner,
    commentListener: CommentsAdapter.Listener,
    val listener: Listener
) :
    CommentsAdapter(emptyList(), commentListener) {

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
                notifyItemRangeChanged(0, getItemsOffset())
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            POST_CONTROLS_TYPE -> PostControlsViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_post_header, parent,
                    false
                ).apply {
                    postComment.visibility = View.GONE
                }
            )
            COMMENT_TITLE_TYPE -> CommentsTitleViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_post_comments_title,
                    parent,
                    false
                )
            )
            COMMENT_TYPE -> super.onCreateViewHolder(parent, viewType)
            LOADING_TYPE -> LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_loading,
                    parent,
                    false
                )
            )

            CONTENT_TEXT_TYPE -> PostTextViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_content_text,
                    parent,
                    false
                )
            )
            CONTENT_IMAGE_TYPE -> PostImageViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_content_image,
                    parent,
                    false
                )
            )
            CONTENT_EMBED_TYPE ->
                PostEmbedViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_content_embed,
                        parent,
                        false
                    )
                )
            else -> throw RuntimeException("Unsupported view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            POST_CONTROLS_TYPE -> {
                holder as PostControlsViewHolder
                postModel?.let {
                    holder.bind(it, listener)
                }
            }
            COMMENT_TITLE_TYPE -> {
                holder as CommentsTitleViewHolder
                holder.bind(postModel?.comments?.count ?: 0)
            }
            COMMENT_TYPE -> super.onBindViewHolder(holder, position - getItemsOffset())
            LOADING_TYPE -> {
                holder as LoadingViewHolder
                holder.bind(isLoading)
            }

            CONTENT_TEXT_TYPE -> {
                holder as PostTextViewHolder
                holder.bind(postModel!!.content.body.full[adapterPositionToContentRowPosition(position)] as TextRowModel)
            }
            CONTENT_IMAGE_TYPE -> {
                holder as PostImageViewHolder
                holder.bind(postModel!!.content.body.full[adapterPositionToContentRowPosition(position)] as ImageRowModel)
            }
            CONTENT_EMBED_TYPE -> {
                holder as PostEmbedViewHolder
                holder.bind(postModel!!.content.body.embeds.first(), lifecycleOwner)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        if (postModel != null) {
            if (position in getPostContentPositionStart() until (postModel!!.content.body.full.size + getPostContentPositionStart())) {
                if (postModel!!.content.body.full.isNotEmpty())
                    return when (postModel!!.content.body.full[adapterPositionToContentRowPosition(position)]) {
                        is TextRowModel -> CONTENT_TEXT_TYPE
                        is ImageRowModel -> CONTENT_IMAGE_TYPE
                    }
            }
            //display only first embed on position 0 for now
            if (getEmbedsCount() > 0 && position == 0)
                return CONTENT_EMBED_TYPE
        }
        return when (position) {
            getPostControlsPosition() -> POST_CONTROLS_TYPE
            getCommentsTitlePosition() -> COMMENT_TITLE_TYPE
            getLoadingViewHolderPosition() -> LOADING_TYPE
            else -> COMMENT_TYPE
        }
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
    private fun getHeadersCount() =
        (postModel?.content?.body?.full?.size ?: 0) + getPostContentPositionStart() + 1

    /**
     * Returns [getItemCount] of superclass but also adds [getItemsOffset] (which is amount of the headers in adapter)
     * and 1 (which is loading view at the bottom)
     */
    override fun getItemCount() = super.getItemCount() + getItemsOffset() + 1

    /**
     * Returns position of loading view holder
     */
    private fun getLoadingViewHolderPosition() = itemCount - 1

    /**
     * Use only first embed if any
     */
    private fun getEmbedsCount() = (if (postModel?.content?.body?.embeds.isNullOrEmpty()) 0 else 1)

    interface Listener {
        fun onPostUpvote(postModel: PostModel)
        fun onPostDownvote(postModel: PostModel)
    }

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

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is PostEmbedViewHolder) {
            //stops webview when offscreen
            holder.onPause()
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is PostEmbedViewHolder) {
            holder.onResume()
        }
    }


}