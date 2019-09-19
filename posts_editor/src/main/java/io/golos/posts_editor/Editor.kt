package io.golos.posts_editor

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import io.golos.posts_editor.components.input.LinkInfo
import io.golos.posts_editor.components.input.edit_text.CustomEditText
import io.golos.posts_editor.models.EditorContent
import io.golos.posts_editor.models.EditorTextStyle
import io.golos.posts_editor.models.RenderType
import io.golos.posts_editor.utilities.MaterialColor

@Suppress("KDocUnresolvedReference")
class Editor(context: Context, attrs: AttributeSet) : EditorCore(context, attrs) {
    /**
     * @param the value is true if some text is selected, otherwise it's false
     */
    private var onSelectionTextChangeListener: ((Boolean) -> Unit)? = null

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

    fun insertLinkInText(text: String, url: String) {
        inputExtensions!!.insertLinkInText(LinkInfo(text, url))
    }

    fun editTag(tag: String) {
        inputExtensions!!.editTag(tag)
    }

    fun editMention(mention: String) {
        inputExtensions!!.editMention(mention)
    }

    fun editLinkInText(text: String, url: String) {
        inputExtensions!!.editLinkInText(LinkInfo(text, url))
    }

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

    fun setDividerLayout(layout: Int) {
        this.dividerExtensions!!.setDividerLayout(layout)
    }

    fun insertDivider() {
        dividerExtensions!!.insertDivider(-1)
    }

    fun openImagePicker() {
        imageExtensions!!.openImageGallery()
    }

    fun insertImage(bitmap: Bitmap) {
        imageExtensions!!.insertImage(bitmap, null, -1, null, true)
    }

    fun onImageUploadComplete(url: String, imageId: String) {
        imageExtensions!!.onPostUpload(url, imageId)
    }

    @Suppress("unused")
    fun onImageUploadFailed(imageId: String) {
        imageExtensions!!.onPostUpload(null, imageId)
    }

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

    private fun render() {
        if (renderType === RenderType.EDITOR) {
            inputExtensions!!.insertEditText(0, null)
        }
    }
}
