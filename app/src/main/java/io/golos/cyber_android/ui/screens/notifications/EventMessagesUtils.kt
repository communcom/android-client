package io.golos.cyber_android.ui.screens.notifications

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import io.golos.cyber_android.R
import io.golos.domain.requestmodel.*

internal val boldSpan = StyleSpan(Typeface.BOLD)

internal fun MentionEventModel.getMessage(context: Context): CharSequence {
    val message = String.format(
        context.resources.getString(R.string.event_mention_format),
        this.actor.id.name,
        this.comment.body
    )
    return SpannableStringBuilder(message).apply {
        setSpan(boldSpan, 0, this@getMessage.actor.id.name.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}

internal fun RepostEventModel.getMessage(context: Context): CharSequence {
    val message = String.format(
        context.resources.getString(R.string.event_repost_format),
        this.actor.id.name,
        this.post.title
    )
    return SpannableStringBuilder(message).apply {
        setSpan(boldSpan, 0, this@getMessage.actor.id.name.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}

internal fun CuratorAwardEventModel.getMessage(context: Context): CharSequence {
    val message = if (this.post != null)
        String.format(
            context.resources.getString(R.string.event_curator_award_post_format),
            this.post?.title
        )
    else
        context.resources.getString(R.string.event_curator_award_format)

    return if (this.post != null)
        SpannableStringBuilder(message).apply {
            setSpan(
                boldSpan,
                message.length - (this@getMessage.post?.title?.length ?: 0),
                message.length,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
    else
        message
}

internal fun ReplyEventModel.getMessage(context: Context): CharSequence {
    val message = String.format(
        context.resources.getString(R.string.event_reply_format),
        this.actor.id.name,
        this.comment.body
    )
    return SpannableStringBuilder(message).apply {
        setSpan(boldSpan, 0, this@getMessage.actor.id.name.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}

internal fun SubscribeEventModel.getMessage(context: Context): CharSequence {
    val message = String.format(
        context.resources.getString(R.string.event_subscribe_format),
        this.actor.id.name
    )
    return SpannableStringBuilder(message).apply {
        setSpan(boldSpan, 0, this@getMessage.actor.id.name.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}

internal fun UnSubscribeEventModel.getMessage(context: Context): CharSequence {
    val message = String.format(
        context.resources.getString(R.string.event_unsubscribe_format),
        this.actor.id.name
    )
    return SpannableStringBuilder(message).apply {
        setSpan(boldSpan, 0, this@getMessage.actor.id.name.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}

internal fun VoteEventModel.getMessage(context: Context): CharSequence {
    val message = if (this.post != null)
        String.format(
            context.resources.getString(R.string.event_upvote_post_format),
            this.actor.id.name,
            this.post?.title
        )
    else
        String.format(
            context.resources.getString(R.string.event_upvote_format),
            this.actor.id.name
        )
    return SpannableStringBuilder(message).apply {
        setSpan(boldSpan, 0, this@getMessage.actor.id.name.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}

internal fun FlagEventModel.getMessage(context: Context): CharSequence {
    val message = if (this.post != null)
        String.format(
            context.resources.getString(R.string.event_downvote_post_format),
            this.actor.id.name,
            this.post?.title
        )
    else
        String.format(
            context.resources.getString(R.string.event_downvote_format),
            this.actor.id.name
        )
    return SpannableStringBuilder(message).apply {
        setSpan(boldSpan, 0, this@getMessage.actor.id.name.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}

internal fun TransferEventModel.getMessage(context: Context): CharSequence {
    val message = String.format(
        context.resources.getString(R.string.event_transfer_format),
        this.value.amount,
        this.value.currency,
        this.actor.id.name
    )

    return SpannableStringBuilder(message).apply {
        setSpan(
            boldSpan,
            message.length - this@getMessage.actor.id.name.length,
            message.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}