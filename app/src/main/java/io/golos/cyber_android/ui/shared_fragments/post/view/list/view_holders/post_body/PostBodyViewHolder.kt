package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body

import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.shared_fragments.post.view.list.list_items.PostBodyListItem
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.*
import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelItemsClickProcessor
import io.golos.domain.post.post_dto.*
import kotlinx.android.synthetic.main.item_content_text.view.*
import timber.log.Timber

class PostBodyViewHolder constructor(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelItemsClickProcessor, PostBodyListItem>(
    parentView,
    R.layout.item_content_text
) {
    override fun init(listItem: PostBodyListItem, listItemEventsProcessor: PostPageViewModelItemsClickProcessor) {
        itemView.errorHolder.visibility = View.INVISIBLE

        try {
            itemView.postWidgetContainer.removeAllViews()

            listItem.post.content.forEach { block ->
                itemView.postWidgetContainer.addView(createWidget(block, listItemEventsProcessor) as View)
            }

            listItem.post.attachments?.let { itemView.postWidgetContainer.addView(createWidget(it, listItemEventsProcessor) as View) }
        } catch (ex: Exception) {
            Timber.e(ex)
            showError(R.string.common_general_error)
        }
    }

    override fun release() {
        with(itemView.postWidgetContainer) {
            for(i in 0 until childCount) {
                getChildAt(i).let {it as? PostBlockWidget<*> }?.cancel()
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun showError(@StringRes errorText: Int) {
        itemView.postWidgetContainer.visibility = View.GONE

        itemView.errorHolder.setText(errorText)
        itemView.errorHolder.visibility = View.VISIBLE
    }

    private fun createWidget(block: Block, listItemEventsProcessor: PostPageViewModelItemsClickProcessor): PostBlockWidget<*> =
        when(block) {
            is AttachmentsBlock -> {
                if(block.content.size == 1) {
                    createWidget(block.content.single(), listItemEventsProcessor) // A single attachment is shown as embed block
                } else {
                    AttachmentsWidget(itemView.context).apply { render(block) }
                }
            }

            is ImageBlock -> EmbedImageWidget(itemView.context).apply {
                render(block)
                setOnClickProcessor(listItemEventsProcessor)
            }

            is VideoBlock -> EmbedVideoWidget(itemView.context).apply { render(block) }

            is WebsiteBlock -> EmbedWebsiteWidget(itemView.context).apply {
                render(block)
                setOnClickProcessor(listItemEventsProcessor)
            }

            is ParagraphBlock -> ParagraphWidget(itemView.context).apply {
                render(block)
                setOnClickProcessor(listItemEventsProcessor)
            }

            else -> throw UnsupportedOperationException("This type of block is not supported: $block")
        }
}