package io.golos.posts_editor

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import io.golos.domain.use_cases.post.TextStyle
import io.golos.domain.use_cases.post.editor_output.ControlMetadata
import io.golos.domain.use_cases.post.editor_output.EmbedType
import io.golos.posts_editor.components.EmbedWidget
import io.golos.posts_editor.components.input.edit_text.CustomEditText
import io.golos.posts_editor.models.EditorContent
import io.golos.posts_editor.models.RenderType
import io.golos.posts_editor.utilities.MaterialColor

@Suppress("KDocUnresolvedReference")
class Editor(context: Context, attrs: AttributeSet) : EditorCore(context, attrs), EditorDataLoader {
    override var editorListener: EditorListener?
        get() = super.editorListener
        set(_listener) {
            super.editorListener = _listener
        }

    public override val content: EditorContent?
        get() = super.content

    public override val contentAsSerialized: String
        get() = super.contentAsSerialized

    init {
        super.editorListener = null
    }

    public override fun getContentAsSerialized(state: EditorContent): String {
        return super.getContentAsSerialized(state)
    }

    public override fun getContentDeserialized(EditorContentSerialized: String): EditorContent {
        return super.getContentDeserialized(EditorContentSerialized)
    }

    public override fun clearAllContents() {
        super.clearAllContents()
        if (renderType === RenderType.EDITOR) {
            inputExtensions!!.insertEditText(0, null)
        }
    }

    fun updateTextStyle(style: TextStyle) {
        inputExtensions!!.updateTextStyle(style, null)
    }

    fun updateTextColor(color: MaterialColor) {
        inputExtensions!!.updateTextColor(color, null)
    }

    fun pastedLinkIsValid(uri: Uri) = inputExtensions!!.lastPastedLinkWasValidated(uri)

    /**
     * Tries to find a tag under a cursor and gets a value of it
     */
    fun tryGetTextOfTag(): String? = inputExtensions!!.tryGetTextOfTag()

    /**
     * Tries to find a mention under a cursor and gets a value of it
     */
    fun tryGetTextOfMention(): String? = inputExtensions!!.tryGetTextOfMention()

    fun insertEmptyParagraph() {
        inputExtensions!!.insertEditText(childCount, null)
    }

    override fun insertParagraph(text: CharSequence) {
        inputExtensions!!.insertEditText(childCount, text)
    }

    override fun insertEmbed(type: EmbedType, sourceUri: Uri, displayUri: Uri, description: String?) =
        embedExtensions!!.insert(type, sourceUri, displayUri, description)

    override fun onKey(v: View, keyCode: Int, event: KeyEvent, editText: CustomEditText): Boolean {
        val onKey = super.onKey(v, keyCode, event, editText)
        if (parentChildCount == 0)
            render()
        return onKey
    }

    @Suppress("unused")
    fun setLineSpacing(lineSpacing: Float) {
        this.inputExtensions!!.setLineSpacing(lineSpacing)
    }

    fun setOnSelectionTextChangeListener(listener: ((Boolean) -> Unit)?) {
        inputExtensions?.setOnSelectionChangeListener(listener)
    }

    fun setOnEmbedAddedOrRemovedListener(listener: ((Boolean) -> Unit)?) {
        embedExtensions?.setOnEmbedAddedOrRemovedListener(listener)
    }

    fun setOnLinkWasPastedListener(listener: ((Uri) -> Unit)?) {
        _onLinkWasPastedListener = listener
    }

    fun getMetadata(): List<ControlMetadata> {
        inputExtensions!!.closeTextTasks()

        val result = mutableListOf<ControlMetadata>()

        for(i in 0 until parentView.childCount) {
            parentView.getChildAt(i)
                .let { inputExtensions!!.getMetadata(it) ?: embedExtensions!!.getMetadata(it) }
                ?.let { result.add(it) }
        }

        return result
    }

    fun getEmbedCount(): Int {
        var counter = 0

        for(i in 0 until parentView.childCount) {
            if(parentView.getChildAt(i) is EmbedWidget) {
                counter++
            }
        }

        return counter
    }

    private fun render() {
        if (renderType === RenderType.EDITOR) {
            inputExtensions!!.insertEditText(0, null)
        }
    }
}
