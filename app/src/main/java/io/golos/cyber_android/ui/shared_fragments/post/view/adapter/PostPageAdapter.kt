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
) : CommentsAdapter(emptyList(), commentListener) {

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

//            CONTENT_IMAGE_TYPE -> PostImageViewHolder(
//                LayoutInflater.from(parent.context).inflate(
//                    R.layout.item_content_image,
//                    parent,
//                    false
//                )
//            )

            CONTENT_EMBED_TYPE -> EmptyViewHolder(parent.context)

            else -> throw RuntimeException("Unsupported view type")
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
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
//            COMMENT_TYPE -> super.onBindViewHolder(holder, position - getItemsOffset())
            LOADING_TYPE -> {
                holder as LoadingViewHolder
                holder.bind(isLoading)
            }

            CONTENT_TEXT_TYPE -> {
                holder as PostTextViewHolder
                postModel?.let { holder.bind(it.content.body.postBlock, recyclerView) }
            }
            CONTENT_EMBED_TYPE -> {     // do nothing
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)

        when (holder) {
            is PostTextViewHolder -> holder.cleanUp()
            else -> {}
        }
    }

//    override fun getItemViewType(position: Int): Int = CONTENT_TEXT_TYPE

    override fun getItemViewType(position: Int): Int {
        if (postModel != null) {
            with(postModel!!.content.body.postBlock) {
                if (position in getPostContentPositionStart() until (/*postModel!!.content.body.full.size*/ 1 + getPostContentPositionStart())) {
                    if (content.isNotEmpty() || attachments?.content?.isNotEmpty() == true)
                        return CONTENT_TEXT_TYPE /*when (postModel!!.content.body.full[adapterPositionToContentRowPosition(position)]) {
                        is TextRowModel -> CONTENT_TEXT_TYPE
                        is ImageRowModel -> CONTENT_IMAGE_TYPE
                    }*/
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
}