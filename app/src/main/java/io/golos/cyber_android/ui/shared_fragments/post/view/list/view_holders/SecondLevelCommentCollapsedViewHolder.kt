package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders

import android.text.SpannableStringBuilder
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.ui.common.characters.SpecialChars
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.SecondLevelCommentCollapsedListItem
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelListEventsProcessor
import io.golos.domain.AppResourcesProvider
import kotlinx.android.synthetic.main.item_post_comment_second_level_collapsed.view.*
import javax.inject.Inject

class SecondLevelCommentCollapsedViewHolder(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelListEventsProcessor, SecondLevelCommentCollapsedListItem>(
    parentView,
    R.layout.item_post_comment_second_level_collapsed
) {
    @ColorInt
    private val spansColor: Int

    @Inject
    internal lateinit var appResourcesProvider: AppResourcesProvider

    init {
        App.injections.get<PostPageFragmentComponent>().inject(this)

        spansColor = appResourcesProvider.getColor(R.color.default_clickable_span_color)
    }

    override fun init(listItem: SecondLevelCommentCollapsedListItem, listItemEventsProcessor: PostPageViewModelListEventsProcessor) {
        loadAvatarIcon(listItem.topCommentAuthor.avatarUrl)
        itemView.replyText.text = getReplyText(listItem)
    }

    private fun loadAvatarIcon(avatarUrl: String?) {
        if (avatarUrl != null) {
            Glide.with(itemView).load(avatarUrl)
        } else {
            Glide.with(itemView).load(R.drawable.ic_empty_user)
        }.apply {
            apply(RequestOptions.circleCropTransform())
            into(itemView.userAvatar)
        }
    }

    private fun getReplyText(listItem: SecondLevelCommentCollapsedListItem) : SpannableStringBuilder {
        val result = SpannableStringBuilder()

        with(listItem) {
            result.append(topCommentAuthor.username)

            result.append(" ")
            result.append(appResourcesProvider.getString(R.string.comment_answer))

            result.append(" ${SpecialChars.bullet} ")

            result.append(totalChild.toString())
            result.append(appResourcesProvider.getQuantityString(R.plurals.reply, totalChild.toInt()))
        }

        return result
    }
}