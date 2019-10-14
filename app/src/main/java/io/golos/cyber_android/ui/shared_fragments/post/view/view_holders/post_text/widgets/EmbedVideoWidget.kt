package io.golos.cyber_android.ui.shared_fragments.post.view.view_holders.post_text.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import io.golos.cyber_android.R
import io.golos.domain.post.post_dto.VideoBlock
import io.golos.posts_editor.utilities.post.PostStubs
import kotlinx.android.synthetic.main.view_post_embed_video.view.*
import kotlinx.android.synthetic.main.view_post_embed_video.view.description
import kotlinx.android.synthetic.main.view_post_embed_video.view.image

class EmbedVideoWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr),
    PostBlockWidget<VideoBlock> {

    init {
        inflate(context, R.layout.view_post_embed_video, this)
    }

    override fun render(block:  VideoBlock) {
        description.text = block.description ?: block.title ?: block.author ?: ""

        if(block.html != null) {
            video.visibility = View.VISIBLE
            image.visibility = View.GONE

            video.loadHtml(block.html)
        } else {
            video.visibility = View.GONE
            image.visibility = View.VISIBLE

            Glide
                .with(this)
                .load(block.thumbnailUrl?.toString() ?: PostStubs.video)
                .into(image)
        }
    }

    override fun cancel() {
        if(video.visibility == View.VISIBLE) {
            video.stopLoading()
        }

        if(image.visibility == View.VISIBLE) {
            Glide.with(this).clear(image)
        }
    }
}