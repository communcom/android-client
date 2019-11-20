package io.golos.cyber_android.ui.shared_fragments.editor.view.dialogs.one_text_line

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.utils.TextWatcherBase
import kotlinx.android.synthetic.main.dailog_one_text_line.view.*

class OneTextLineDialogView
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

    init {
        inflate(context, R.layout.dailog_one_text_line, this)

        textField.addTextChangedListener(object: TextWatcherBase() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)

                val isTextEmptyNew = s.isNullOrBlank()

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