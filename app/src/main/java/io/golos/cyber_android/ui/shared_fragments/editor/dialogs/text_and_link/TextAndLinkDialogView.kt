package io.golos.cyber_android.ui.shared_fragments.editor.dialogs.text_and_link

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.helper.UIHelper
import kotlinx.android.synthetic.main.dailog_text_and_link.view.*
import javax.inject.Inject

class TextAndLinkDialogView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    @Inject
    internal lateinit var uiHelper: UIHelper

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
    }
}