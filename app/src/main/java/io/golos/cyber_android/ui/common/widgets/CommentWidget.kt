package io.golos.cyber_android.ui.common.widgets

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.glide.loadCommentAttachment
import io.golos.cyber_android.ui.common.glide.release
import io.golos.cyber_android.ui.dto.Comment
import io.golos.cyber_android.ui.utils.TextWatcherBase
import io.golos.domain.use_cases.post.post_dto.ContentBlock
import io.golos.domain.use_cases.post.post_dto.ImageBlock
import io.golos.domain.use_cases.post.post_dto.ParagraphBlock
import io.golos.domain.use_cases.post.post_dto.TextBlock
import kotlinx.android.synthetic.main.layout_comment_edit_block.view.*
import kotlinx.android.synthetic.main.layout_comment_image_attachment.view.*
import kotlinx.android.synthetic.main.layout_comment_input.view.*
import kotlinx.android.synthetic.main.layout_comment_input.view.root
import kotlinx.android.synthetic.main.widget_comment.view.*
import kotlin.properties.Delegates


class CommentWidget @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var onSendClickListener: ((ContentBlock) -> Unit)? = null

    //private var currentContentBlock: ContentBlock? = null

    private var editComment: Comment? = null

    private var attachmentImageUrl by Delegates.observable<String?>(null) { _, oldUrl, newUrl ->
        if (newUrl != oldUrl) {
            if(TextUtils.isEmpty(newUrl)){
                commentAttachment.visibility = View.GONE
                sendButton.visibility = if (comment.text.isNullOrBlank()) View.GONE else View.VISIBLE
                ivEditAttachment.visibility = View.GONE
            } else{
                commentAttachment.visibility = View.VISIBLE
                ivEditAttachment.visibility = View.VISIBLE
                sendButton.visibility = View.VISIBLE
                ivEditAttachment.loadCommentAttachment(newUrl)
                ivAttachment.loadCommentAttachment(newUrl)
            }
        }
    }

    init {
        inflate(context, R.layout.widget_comment, this)

        root.layoutTransition.setDuration(context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())

        comment.addTextChangedListener(object : TextWatcherBase() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)

                sendButton.visibility = if (s.isNullOrBlank() && attachmentImageUrl == null) View.GONE else View.VISIBLE
            }
        })
        sendButton.setOnClickListener {
            /*currentContentBlock?.let {
                onSendClickListener?.invoke(it)
            }*/
        }
        closeButton.setOnClickListener {
            clear()
            visibility = View.GONE
        }
        deleteAttachment.setOnClickListener{
            attachmentImageUrl = null
        }
    }

    fun clear(){
        //currentContentBlock = null
        editComment = null
        attachmentImageUrl = null
        clearComment()
        clearEditState()
        clearAttachmentState()
    }

    fun setCommentForEdit(comment: Comment){
        editComment = comment
        val body = comment.body
        val content = body?.content ?: listOf()
        if(content.isNotEmpty()){
            val block = content[0]
            if(block is ParagraphBlock){
                val paragraphItemBlock = block.content.firstOrNull()
                if(paragraphItemBlock != null && paragraphItemBlock is TextBlock){
                    val text = paragraphItemBlock.content
                    oldText.text = text
                    this.comment.setText(text)
                } else{
                    oldText.text = null
                }
            } else{
                oldText.text = null
            }
        }
        val mediaBlock = body?.attachments?.content?.firstOrNull()
        if(mediaBlock is ImageBlock){
            attachmentImageUrl = mediaBlock.content.toString()
        }
        commentEdit.visibility = View.VISIBLE
    }

    fun setOnSendClickListener(listener: ((ContentBlock) -> Unit)?) {
        onSendClickListener = listener
    }

    private fun clearEditState(){
        editComment = null
        commentEdit.visibility = View.GONE
        ivAttachment.release()
        ivEditAttachment.release()
    }

    private fun clearAttachmentState(){
        commentAttachment.visibility = View.GONE
        ivAttachment.release()
    }

    private fun clearComment() {
        comment.setText("")
    }
}