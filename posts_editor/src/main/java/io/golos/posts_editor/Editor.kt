package io.golos.posts_editor

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import io.golos.domain.post_editor.*
import io.golos.posts_editor.components.input.edit_text.CustomEditText
import io.golos.posts_editor.models.EditorContent
import io.golos.posts_editor.components.EmbedWidget
import io.golos.posts_editor.models.RenderType
import io.golos.posts_editor.utilities.MaterialColor

@Suppress("KDocUnresolvedReference")
class Editor(context: Context, attrs: AttributeSet) : EditorCore(context, attrs) {
    /**
     * @param the value is true if some text is selected, otherwise it's false
     */
    private var onSelectionTextChangeListener: ((Boolean) -> Unit)? = null

    /**
     * @param the value is true if some text is selected, otherwise it's false
     */
    private var onEmbedAddedOrRemovedListener: ((Boolean) -> Unit)? = null

    override var editorListener: EditorListener?
        get() = super.editorListener
        set(_listener) {
            super.editorListener = _listener
        }

    public override val content: EditorContent?
        get() = super.content

    public override val contentAsSerialized: String
        get() = super.contentAsSerialized

    val contentAsHTML: String
        get() = htmlContent

    init {
        super.editorListener = null
        inputExtensions!!.insertEditText(childCount, null)
    }

    public override fun getContentAsSerialized(state: EditorContent): String {
        return super.getContentAsSerialized(state)
    }

    public override fun getContentDeserialized(EditorContentSerialized: String): EditorContent {
        return super.getContentDeserialized(EditorContentSerialized)
    }

    fun getContentAsHTML(content: EditorContent): String {
        return getHTMLContent(content)
    }

    fun getContentAsHTML(editorContentAsSerialized: String): String {
        return getHTMLContent(editorContentAsSerialized)
    }

    fun render(_state: EditorContent) {
        super.renderEditor(_state)
    }

    fun render(HtmlString: String) {
        renderEditorFromHtml(HtmlString)
    }

    public override fun clearAllContents() {
        super.clearAllContents()
        if (renderType === RenderType.EDITOR) {
            inputExtensions!!.insertEditText(0, null)
        }
    }

    fun updateTextStyle(style: EditorTextStyle) {
        inputExtensions!!.updateTextStyle(style, null)
    }

    fun updateTextColor(color: MaterialColor) {
        inputExtensions!!.updateTextColor(color, null)
    }

    fun insertLink() {
        inputExtensions!!.insertLink()
    }

    @Suppress("unused")
    fun insertLink(link: String) {
        inputExtensions!!.insertLink(link)
    }

    fun insertTag(tag: String) {
        inputExtensions!!.insertTag(tag)
    }

    fun insertMention(mention: String) {
        inputExtensions!!.insertMention(mention)
    }

    fun insertLinkInText(linkInfo: LinkInfo) = inputExtensions!!.insertLinkInText(linkInfo)

    fun editTag(tag: String) {
        inputExtensions!!.editTag(tag)
    }

    fun editMention(mention: String) {
        inputExtensions!!.editMention(mention)
    }

    fun editLinkInText(linkInfo: LinkInfo) = inputExtensions!!.editLinkInText(linkInfo)

    /**
     * Tries to find a tag under a cursor and gets a value of it
     */
    fun tryGetTextOfTag(): String? = inputExtensions!!.tryGetTextOfTag()

    /**
     * Tries to find a mention under a cursor and gets a value of it
     */
    fun tryGetTextOfMention(): String? = inputExtensions!!.tryGetTextOfMention()

    /**
     * Tries to find a link under a cursor and gets a value of it
     */
    fun tryGetLinkInTextInfo(): LinkInfo? = inputExtensions!!.tryGetLinkInTextInfo()

    fun openImagePicker() {
        embedExtensions!!.openImageGallery()
    }

    fun insertEmbed(type: EmbedType, sourceUri: Uri, displayUri: Uri, description: String?) =
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

    fun getMetadata(): List<ControlMetadata> {
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
