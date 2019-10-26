package io.golos.cyber_android.ui.common.widgets

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.UIComponent
import io.golos.cyber_android.ui.common.utils.TextWatcherBase
import io.golos.domain.AppResourcesProvider
import kotlinx.android.synthetic.main.view_comment_widget_edit.view.*
import javax.inject.Inject

class CommentWidgetEdit
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onSendClickListener: ((String) -> Unit)? = null
    private var onCloseClickListener: (() -> Unit)? = null

    @Inject
    internal lateinit var appResourcesProvider: AppResourcesProvider

    init {
        App.injections.get<UIComponent>().inject(this)

        inflate(context, R.layout.view_comment_widget_edit, this)

        root.layoutTransition.setDuration(appResourcesProvider.getInteger(android.R.integer.config_shortAnimTime).toLong())

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

    fun setText(text: List<CharSequence>) {
        val builder = SpannableStringBuilder()

        var isFirstParagraph = true
        text.forEach {
            if(!isFirstParagraph) {
                builder.append("\n")
            }
            isFirstParagraph = false
            builder.append(it)
        }

        newText.text = builder
        oldText.text = text.firstOrNull()
    }
}