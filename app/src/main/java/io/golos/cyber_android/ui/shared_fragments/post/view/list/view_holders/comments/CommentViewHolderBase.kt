package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.comments

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.characters.SpecialChars
import io.golos.cyber_android.ui.common.extensions.loadAvatar
import io.golos.cyber_android.ui.common.formatters.time_estimation.TimeEstimationFormatter
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.spans.ColorTextClickableSpan
import io.golos.cyber_android.ui.common.spans.StyledColorTextClickableSpan
import io.golos.cyber_android.ui.common.spans.StyledTextClickableSpan
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.CommentListItem
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.CommentListItemState
import io.golos.cyber_android.ui.shared_fragments.post.helpers.CommentTextRenderer
import io.golos.cyber_android.ui.shared_fragments.post.view.widgets.VotingWidget
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelListEventsProcessor
import io.golos.domain.AppResourcesProvider
import io.golos.domain.extensions.appendSpannedText
import io.golos.domain.interactors.model.DiscussionAuthorModel
import io.golos.domain.interactors.model.DiscussionMetadataModel
import io.golos.domain.interactors.model.DiscussionVotesModel
import io.golos.domain.post.post_dto.PostBlock
import javax.inject.Inject

@Suppress("PropertyName")
abstract class CommentViewHolderBase<T: CommentListItem>(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelListEventsProcessor, T>(
    parentView,
    R.layout.item_post_comment
) {
    private val maxStringLenToCutNeeded = 285

    @ColorInt
    private val spansColor: Int

    @ColorInt
    private val moreLabelColor: Int

    @Inject
    internal lateinit var appResourcesProvider: AppResourcesProvider

    @Inject
    internal lateinit var commentTextRenderer: CommentTextRenderer

    abstract val _userAvatar: ImageView

    abstract val _voting: VotingWidget

    abstract val _mainCommentText: TextView

    abstract val _replyAndTimeText: TextView

    abstract val _processingProgress: ProgressBar

    abstract val _warning: ImageView

    abstract val _rootView: View

    init {
        @Suppress("LeakingThis")
        inject()

        spansColor = appResourcesProvider.getColor(R.color.default_clickable_span_color)
        moreLabelColor = appResourcesProvider.getColor(R.color.dark_gray)
    }

    @CallSuper
    override fun init(listItem: T, listItemEventsProcessor: PostPageViewModelListEventsProcessor) {
        loadAvatarIcon(listItem.author.avatarUrl)

        _mainCommentText.text = getCommentText(
            listItem.author,
            getParentAuthor(listItem),
            listItem.currentUserId,
            listItem.content,
            true)

        _replyAndTimeText.text = getReplyAndTimeText(listItem.metadata)

        setVoting(listItem.votes)

        setProcessingState(listItem.state)

        if(listItem.author.userId.userId == listItem.currentUserId) {
            _rootView.setOnLongClickListener {
                if(listItem.state != CommentListItemState.PROCESSING) {
                    listItemEventsProcessor.onCommentLongClick(listItem.externalId)
                }
                true
            }
        }
    }

    @CallSuper
    override fun release() {
        _rootView.setOnLongClickListener {
            false
        }
    }

    abstract fun inject()

    abstract fun getParentAuthor(listItem: T): DiscussionAuthorModel?

    private fun loadAvatarIcon(avatarUrl: String?) = _userAvatar.loadAvatar(avatarUrl)

    private fun getCommentText(
        author: DiscussionAuthorModel,
        parentAuthor: DiscussionAuthorModel?,
        currentUserId: String,
        content: PostBlock,
        cutIfNeeded: Boolean): SpannableStringBuilder {

        val result = SpannableStringBuilder()

        // Author
        if (author.userId.userId == currentUserId) {
            result.appendSpannedText(author.username, StyleSpan(Typeface.BOLD))
        } else {
            val span = object : StyledTextClickableSpan(author.username, Typeface.DEFAULT_BOLD) {
                override fun onClick(spanData: String) {
                    // user name
                }
            }
            result.appendSpannedText(author.username, span)
        }

        result.append(" ")

        // Parent author
        if(parentAuthor != null && parentAuthor.userId.userId != currentUserId) {
            val span = object : StyledColorTextClickableSpan(author.username, Typeface.DEFAULT_BOLD, spansColor) {
                override fun onClick(spanData: String) {
                    // user name
                }
            }
            result.appendSpannedText(parentAuthor.username, span)
            result.append(" ")
        }

        // Paragraphs
        var isFirstParagraph = true
        val paragraphs = commentTextRenderer.render(content.content)

        paragraphs.forEach {
            if(!isFirstParagraph) {
                result.append("\n")
            }
            isFirstParagraph = false
            result.append(it)
        }

        // Cut long text if we need it
        if (cutIfNeeded && result.length > maxStringLenToCutNeeded) {
            val cutResult = SpannableStringBuilder()
            cutResult.append(result.subSequence(0 until maxStringLenToCutNeeded))

            cutResult.append("${SpecialChars.ellipsis} ")

            val style = object : StyledColorTextClickableSpan("", Typeface.DEFAULT_BOLD, moreLabelColor) {
                override fun onClick(spanData: String) {
                    // expand text
                }
            }
            cutResult.appendSpannedText(appResourcesProvider.getString(R.string.comments_more), style)

            return cutResult
        }

        return result
    }

    private fun setVoting(votes: DiscussionVotesModel) {
        _voting.setVoteBalance(votes.upCount - votes.downCount)
        _voting.setUpVoteButtonSelected(votes.hasUpVote)
        _voting.setDownVoteButtonSelected(votes.hasDownVote)
    }

    private fun getReplyAndTimeText(metadata: DiscussionMetadataModel): SpannableStringBuilder {
        val result = SpannableStringBuilder()

        // Reply label
        val replySpan = object: ColorTextClickableSpan("", spansColor) {
            override fun onClick(spanData: String) {
                // Reply
            }
        }
        result.appendSpannedText(appResourcesProvider.getString(R.string.reply), replySpan)

        // Time
        val time = TimeEstimationFormatter(appResourcesProvider).format(metadata.time)
        result.append(" ${SpecialChars.bullet} ")
        result.append(time)

        return result
    }

    private fun setProcessingState(state: CommentListItemState) {
        _processingProgress.visibility = if(state == CommentListItemState.PROCESSING) View.VISIBLE else View.INVISIBLE
        _warning.visibility = if(state == CommentListItemState.ERROR) View.VISIBLE else View.INVISIBLE
    }
}
