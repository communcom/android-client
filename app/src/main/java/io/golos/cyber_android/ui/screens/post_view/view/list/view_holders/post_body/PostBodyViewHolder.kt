package io.golos.cyber_android.ui.screens.post_view.view.list.view_holders.post_body

import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.recycler_view.ViewHolderBase
import io.golos.cyber_android.ui.common.widgets.post_comments.*
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.PostBodyListItem
import io.golos.cyber_android.ui.screens.post_view.view_model.PostPageViewModelListEventsProcessor
import io.golos.domain.use_cases.post.post_dto.*
import kotlinx.android.synthetic.main.item_post_block.view.*
import timber.log.Timber

class PostBodyViewHolder constructor(
    parentView: ViewGroup
) : ViewHolderBase<PostPageViewModelListEventsProcessor, PostBodyListItem>(
    parentView,
    R.layout.item_post_block
) {
    override fun init(listItem: PostBodyListItem, listItemEventsProcessor: PostPageViewModelListEventsProcessor) {
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
                getChildAt(i).let {it as? BlockWidget<*, *> }?.release()
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun showError(@StringRes errorText: Int) {
        itemView.postWidgetContainer.visibility = View.GONE

        itemView.errorHolder.setText(errorText)
        itemView.errorHolder.visibility = View.VISIBLE
    }

    private fun createWidget(block: Block, listItemEventsProcessor: PostPageViewModelListEventsProcessor): BlockWidget<*, *> =
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
                setSeeMoreEnabled(false)
                setOnClickProcessor(listItemEventsProcessor)
            }

            is RichBlock -> RichWidget(itemView.context).apply {
                setOnClickProcessor(listItemEventsProcessor)
                render(block)
            }

            is EmbedBlock -> EmbedWidget(itemView.context).apply {
                setOnClickProcessor(listItemEventsProcessor)
                render(block)
            }

            else -> throw UnsupportedOperationException("This type of block is not supported: $block")
        }
}