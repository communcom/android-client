package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.comments

import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.ColorInt
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.characters.SpecialChars
import io.golos.cyber_android.ui.common.formatters.time_estimation.TimeEstimationFormatter
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.common.spans.ColorTextClickableSpan
import io.golos.cyber_android.ui.common.spans.LinkClickableSpan
import io.golos.cyber_android.ui.common.spans.StyledColorTextClickableSpan
import io.golos.cyber_android.ui.common.spans.StyledTextClickableSpan
import io.golos.cyber_android.ui.shared_fragments.post.view.widgets.VotingWidget
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelItemsClickProcessor
import io.golos.domain.AppResourcesProvider
import io.golos.domain.extensions.appendSpannedText
import io.golos.domain.extensions.appendText
import io.golos.domain.extensions.setSpan
import io.golos.domain.interactors.model.DiscussionAuthorModel
import io.golos.domain.interactors.model.DiscussionMetadataModel
import io.golos.domain.interactors.model.DiscussionVotesModel
import io.golos.domain.post.post_dto.*
import io.golos.domain.post.toTypeface
import javax.inject.Inject

@Suppress("PropertyName")
abstract class CommentViewHolderBase<T: VersionedListItem>(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelItemsClickProcessor, T>(
    parentView,
    R.layout.item_post_comment
) {
    private val maxStringLenToCutNeeded = 330

    @ColorInt
    private val spansColor: Int

    @ColorInt
    private val moreLabelColor: Int

    @Inject
    internal lateinit var appResourcesProvider: AppResourcesProvider

    abstract val _userAvatar: ImageView
    abstract val _voting: VotingWidget

    init {
        @Suppress("LeakingThis")
        inject()

        spansColor = appResourcesProvider.getColor(R.color.default_clickable_span_color)
        moreLabelColor = appResourcesProvider.getColor(R.color.dark_gray)
    }

    abstract fun inject()

    protected fun loadAvatarIcon(avatarUrl: String?) {
        if (avatarUrl != null) {
            Glide.with(itemView).load(avatarUrl)
        } else {
            Glide.with(itemView).load(R.drawable.ic_empty_user)
        }.apply {
            apply(RequestOptions.circleCropTransform())
            into(_userAvatar)
        }
    }

    protected fun getCommentText(
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
        content.content.forEach {
            if (it is ParagraphBlock) {
                result.append(getParagraphText(it))
            }
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

    protected fun setVoting(votes: DiscussionVotesModel) {
        _voting.setVoteBalance(votes.upCount - votes.downCount)
        _voting.setUpVoteButtonSelected(votes.hasUpVote)
        _voting.setDownVoteButtonSelected(votes.hasDownVote)
    }

    protected fun getReplyAndTimeText(metadata: DiscussionMetadataModel): SpannableStringBuilder {
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

    private fun getParagraphText(block: ParagraphBlock): CharSequence {
        val builder = SpannableStringBuilder()

        block.content.forEach { blockItem ->
            when (blockItem) {
                is TextBlock -> addText(blockItem, builder)
                is TagBlock -> addTag(blockItem, builder)
                is MentionBlock -> addMention(blockItem, builder)
                is LinkBlock -> addLink(blockItem, builder)
            }
        }

        return builder
    }

    private fun addText(block: TextBlock, builder: SpannableStringBuilder) {
        val textInterval = builder.appendText(block.content)

        block.textColor?.let { builder.setSpan(ForegroundColorSpan(it), textInterval) }
        block.style?.let { builder.setSpan(StyleSpan(it.toTypeface()), textInterval) }
    }

    private fun addTag(block: TagBlock, builder: SpannableStringBuilder) {
        val textInterval = builder.appendText("#${block.content}")

        builder.setSpan(ForegroundColorSpan(spansColor), textInterval)
    }

    private fun addMention(block: MentionBlock, builder: SpannableStringBuilder) {
        val textInterval = builder.appendText("@${block.content}")

        // Click on the link
        builder.setSpan(object : ColorTextClickableSpan(block.content, spansColor) {
            override fun onClick(spanData: String) {
                // User's name
            }
        }, textInterval)
    }

    private fun addLink(block: LinkBlock, builder: SpannableStringBuilder) {
        val textInterval = builder.appendText(block.content)

        // Click on the link
        builder.setSpan(object : LinkClickableSpan(block.url, spansColor) {
            override fun onClick(spanData: Uri) {
                // onClickProcessor?.onLinkInPostClick(spanData)
            }
        }, textInterval)
    }
}
