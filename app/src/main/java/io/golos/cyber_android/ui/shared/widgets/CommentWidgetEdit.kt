package io.golos.cyber_android.ui.shared.widgets

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.di.UIComponent
import io.golos.cyber_android.ui.shared.utils.TextWatcherBase
import io.golos.cyber_android.ui.screens.post_view.dto.EditReplyCommentSettings
import kotlinx.android.synthetic.main.view_comment_widget_edit.view.*

@Deprecated("Need use CommentWidget")
class CommentWidgetEdit
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onSendClickListener: ((String) -> Unit)? = null
    private var onCloseClickListener: (() -> Unit)? = null

    init {
        App.injections.getBase<UIComponent>().inject(this)

        inflate(context, R.layout.view_comment_widget_edit, this)

        root.layoutTransition.setDuration(context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())

        newText.addTextChangedListener(object : TextWatcherBase() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)

                sendButton.visibility = if(s.isNullOrBlank()) View.GONE else View.VISIBLE
            }
        })

        sendButton.setOnClickListener { onSendClickListener?.invoke(newText.text.toString()) }
        closeButton.setOnClickListener { onCloseClickListener?.invoke() }
    }

    fun setOnSendClickListener(listener: ((String) -> Unit)?) {
        onSendClickListener = listener
    }

    fun setOnCloseClickListener(listener: (() -> Unit)?) {
        onCloseClickListener = listener
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)

        newText.isEnabled = enabled
        sendButton.isEnabled = enabled
        closeButton.isEnabled = enabled
    }

    fun updateSettings(settings: EditReplyCommentSettings) {
        val builder = SpannableStringBuilder()

        var isFirstParagraph = true
        settings.newText.forEach {
            if(!isFirstParagraph) {
                builder.append("\n")
            }
            isFirstParagraph = false
            builder.append(it)
        }

        newText.text = builder
        oldText.text = settings.oldText.firstOrNull()

        editCommentLabel.text = if(settings.isInEditMode) {
            context.resources.getString(R.string.edit_comment_send)
        } else {
            context.resources.getString(R.string.edit_comment_reply)
        }
    }
}