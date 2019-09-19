package io.golos.cyber_android.ui.shared_fragments.editor.dialogs.text_and_link

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.views.utils.TextWatcherBase
import kotlinx.android.synthetic.main.dailog_text_and_link.view.*

class TextAndLinkDialogView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var textIsEmptyListener: ((Boolean) -> Unit)? = null

    var isTextEmpty = true
        private set

    var text: String
        get() = textField.text.toString()
        set(value) {
            textField.setText(value)
        }

    var link: String
        get() = linkField.text.toString()
        set(value) {
            linkField.setText(value)
        }

    init {
        inflate(context, R.layout.dailog_text_and_link, this)

        textField.addTextChangedListener(object: TextWatcherBase() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)

                val isTextEmptyNew = s.isNullOrBlank() || link.isBlank()

                if(isTextEmptyNew != isTextEmpty) {
                    textIsEmptyListener?.invoke(isTextEmptyNew)
                }

                isTextEmpty = isTextEmptyNew
            }
        })

        linkField.addTextChangedListener(object: TextWatcherBase() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)

                val isTextEmptyNew = s.isNullOrBlank() || text.isBlank()

                if(isTextEmptyNew != isTextEmpty) {
                    textIsEmptyListener?.invoke(isTextEmptyNew)
                }

                isTextEmpty = isTextEmptyNew
            }
        })
    }

    fun setTextIsEmptyListener(listener: ((Boolean) -> Unit)?) {
        textIsEmptyListener = listener
    }
}