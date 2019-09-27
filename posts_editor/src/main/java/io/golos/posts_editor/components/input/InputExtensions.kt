package io.golos.posts_editor.components.input

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.text.*
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.util.Linkify
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import io.golos.domain.post_editor.*
import io.golos.posts_editor.EditorComponent
import io.golos.posts_editor.EditorCore
import io.golos.posts_editor.R
import io.golos.posts_editor.components.ComponentsWrapper
import io.golos.posts_editor.components.input.edit_text.CustomEditText
import io.golos.posts_editor.components.input.spans.calculators.ColorSpansCalculator
import io.golos.posts_editor.components.input.spans.calculators.CreateSpanOperation
import io.golos.posts_editor.components.input.spans.calculators.DeleteSpanOperation
import io.golos.posts_editor.components.input.spans.calculators.StyleSpansCalculator
import io.golos.posts_editor.components.input.spans.custom.LinkSpan
import io.golos.posts_editor.components.input.spans.custom.MentionSpan
import io.golos.posts_editor.components.input.spans.custom.TagSpan
import io.golos.posts_editor.components.input.spans.spans_worker.SpansWorkerImpl
import io.golos.posts_editor.components.util.mapTypefaceToEditorTextStyle
import io.golos.posts_editor.models.*
import io.golos.posts_editor.utilities.MaterialColor
import io.golos.posts_editor.utilities.Utilities
import io.golos.posts_editor.utilities.fromHtml
import io.golos.posts_editor.utilities.toHtml
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*
import kotlin.reflect.KClass

@Suppress("KDocUnresolvedReference")
class InputExtensions(internal var editorCore: EditorCore) : EditorComponent<ParagraphMetadata>(editorCore) {
    var defaultTextColor = Color.BLACK

    var normalTextSize = 16

    private var lineSpacing = -1f

    @ColorInt
    private val specialSpansColors = Color.BLUE

    private val editor: CustomEditText
        get() = editorCore.activeView as CustomEditText

    /**
     * @param the value is true if some text is selected, otherwise it's false
     */
    private var onSelectionChangeListener: ((Boolean) -> Unit)? = null

    /**
     * @return null if getting metadata from the view is impossible
     */
    override fun getMetadata(view: View): ParagraphMetadata? =
        (view as? CustomEditText)?.let { textEdit ->
            val spansWorker = SpansWorkerImpl(textEdit.text)

            val spans = mutableListOf<SpanInfo<*>>()

            spansWorker.getSpansWithIntervals<CharacterStyle>(CharacterStyle::class)
                .forEach {
                    if(it.spanInterval.last != it.spanInterval.first) {
                        val spanInfo =
                            when(it.span) {
                                is ForegroundColorSpan -> ColorSpanInfo(
                                    it.spanInterval,
                                    it.span.foregroundColor
                                )
                                is StyleSpan -> StyleSpanInfo(
                                    it.spanInterval,
                                    it.span.style.mapTypefaceToEditorTextStyle()
                                )
                                is LinkSpan -> LinkSpanInfo(it.spanInterval, it.span.value)
                                is TagSpan -> TagSpanInfo(it.spanInterval, it.span.value)
                                is MentionSpan -> MentionSpanInfo(it.spanInterval, it.span.value)
                                else -> null
                            }

                        if(spanInfo != null) {
                            spans.add(spanInfo)
                        }

                    }
                }

            val planText = textEdit.text.toString()

            if(planText.isEmpty()) {
                null
            } else {
                ParagraphMetadata(planText, spans)
            }
        }

    override fun getContent(view: View): Node =
        this.getNodeInstance(view)
            .apply {
                content!!.add((view as EditText).text.toHtml())
            }

    override fun getContentAsHTML(node: Node, content: EditorContent): String = getInputHtml()

    override fun renderEditorFromState(node: Node, content: EditorContent) {
        val text = node.content!![0]
        val view = insertEditText(editorCore.childCount, text)
        applyTextSettings(node, view)
    }

    override fun buildNodeFromHTML(element: Element): Node? {
        val text: String
        val count: Int
        val tv: TextView

        when (HtmlTag.valueOf(element.tagName().toLowerCase(Locale.ROOT))) {
            HtmlTag.p, HtmlTag.div -> {
                text = element.html()
                count = editorCore.parentView.childCount
                tv = insertEditText(count, text)
                applyStyles(tv, element)
            }

            else -> {}
        }
        return null
    }

    override fun init(componentsWrapper: ComponentsWrapper) {
        this.componentsWrapper = componentsWrapper
    }

    fun setText(textView: TextView, text: CharSequence) {
        val toReplace = getSanitizedHtml(text)
        textView.text = toReplace
    }

    fun insertTag(tagText: String) = insertTag(tagText, true, false)

    fun insertMention(userName: String) = insertMention(userName, true, false)

    fun insertLinkInText(linkInfo: LinkInfo) = insertLinkInText(linkInfo, true, false)

    fun editTag(tagText: String) {
        if(removeSpecialSpan(TagSpan::class)) {
            editor.post {
                insertTag(tagText, false, true)
            }
        }
    }

    fun editMention(userName: String) {
        if(removeSpecialSpan(MentionSpan::class)) {
            editor.post {
                insertMention(userName, false, true)
            }
        }
    }

    fun editLinkInText(linkInfo: LinkInfo) {
        if(removeSpecialSpan(LinkSpan::class)) {
            editor.post {
                insertLinkInText(linkInfo, false, true)
            }
        }
    }

    /**
     * Tries to find a tag under a cursor and gets a value of it
     */
    fun tryGetTextOfTag(): String? = getSpecialSpanData(TagSpan::class) {
        (it as TagSpan).value
    }?.second

    /**
     * Tries to find a mention under a cursor and gets a value of it
     */
    fun tryGetTextOfMention(): String? = getSpecialSpanData(MentionSpan::class) {
        (it as MentionSpan).value
    }?.second

    /**
     * Tries to find a link under a cursor and gets a value of it
     */
    fun tryGetLinkInTextInfo(): LinkInfo? = getSpecialSpanData(LinkSpan::class) {
        (it as LinkSpan).value
    }?.second

    @Suppress("UNCHECKED_CAST")
    fun updateTextColor(color: MaterialColor, editText: TextView?) {
        try {
            val editTextLocal = (editText ?: editor) as CustomEditText

            // Process the operation only if a selection area exists
            editTextLocal.selectionArea?.let { selection ->
                val spansWorker = SpansWorkerImpl(editTextLocal.text)

                val calculator = ColorSpansCalculator(spansWorker)
                val spanOperations = calculator.calculate(selection, MaterialColor.toSystemColor(color, editTextLocal.context))

                spanOperations.forEach { operation ->
                    when(operation) {
                        is DeleteSpanOperation -> spansWorker.removeSpan(operation.span)

                        is CreateSpanOperation<*> -> {
                            with((operation as CreateSpanOperation<Int>).spanInfo) {
                                spansWorker.createSpan(ForegroundColorSpan(value), area)
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun setLineSpacing(textView: TextView, lineHeight: Float) {
        val fontHeight = textView.paint.getFontMetricsInt(null)
        textView.setLineSpacing((Utilities.dpToPx(editorCore.context, lineHeight) - fontHeight).toFloat(), 1f)
    }

    fun setOnSelectionChangeListener(listener: ((Boolean) -> Unit)?) {
        onSelectionChangeListener = listener
    }

    private fun getNewEditTextInst(hint: String?, text: CharSequence?): CustomEditText {
        val editText = CustomEditText(ContextThemeWrapper(this.editorCore.context, R.style.WysiwygEditText))
        addEditableStyling(editText)
        editText.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        if (text != null) {
            setText(editText, text)
        }

        @Suppress("DEPRECATION")
        editText.setBackgroundDrawable(ContextCompat.getDrawable(this.editorCore.context, R.drawable.invisible_edit_text))

        editText.setOnKeyListener { v, keyCode, event ->
            editorCore.onKey(v, keyCode, event, editText)
        }

        editText.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                editText.clearFocus()

                editText.hint = null
            } else {
                editorCore.activeView = v

                if (hint != null) {
                    editText.hint = hint
                }
            }
        }

        editText.addTextChangedListener(object : TextWatcher {
            private var updateRange: IntRange? = null

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                updateRange = start..start+count
            }

            override fun afterTextChanged(s: Editable) {
                val tag = editText.getTag(R.id.control_tag)

                if (s.isEmpty() && tag != null)
                    editText.hint = tag.toString()

                var isEnterPressed = false

                if (s.isNotEmpty()) {
                    // if user had pressed enter, replace it with br
                    for (i in s.indices) {
                        if (s[i] == '\n') {
                            isEnterPressed = true

                            s.delete(i, i+1)

                            val index = editorCore.parentView.indexOfChild(editText)
                            val position = index + 1
                            val editable = SpannableStringBuilder()

                            insertEditText(position, editable)
                            break
                        }
                    }

                    if(!isEnterPressed) {
                        val spansWorker = SpansWorkerImpl(s)

                        // If a span is edited by user we should remove it
                        val tagsSpans = spansWorker.getSpans<TagSpan>(TagSpan::class, updateRange!!)
                        tagsSpans.forEach { spansWorker.removeSpan(it) }

                        val mentionsSpans = spansWorker.getSpans<MentionSpan>(MentionSpan::class, updateRange!!)
                        mentionsSpans.forEach { spansWorker.removeSpan(it) }

                        val linksSpans = spansWorker.getSpans<LinkSpan>(LinkSpan::class, updateRange!!)
                        linksSpans.forEach { spansWorker.removeSpan(it) }
                    }
                }
                editorCore.editorListener?.onTextChanged(editText, s)
            }
        })

        editText.customSelectionActionModeCallback = object: android.view.ActionMode.Callback {
            override fun onActionItemClicked(mode: android.view.ActionMode?, item: MenuItem?): Boolean = false

            override fun onCreateActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
                onSelectionChangeListener?.invoke(true)
                return true
            }

            override fun onPrepareActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean = false

            override fun onDestroyActionMode(mode: android.view.ActionMode?) {
                onSelectionChangeListener?.invoke(false)
            }
        }

        if (this.lineSpacing != -1f) {
            setLineSpacing(editText, this.lineSpacing)
        }
        return editText
    }

    private fun isLastText(index: Int): Boolean {
        if (index == 0)
            return false
        val view = editorCore.parentView.getChildAt(index - 1)
        val type = editorCore.getControlType(view)
        return type === EditorType.INPUT
    }

    private fun addEditableStyling(editText: TextView) {
        editText.isFocusableInTouchMode = true
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, normalTextSize.toFloat())
        editText.setTextColor(this.defaultTextColor)
        editText.setPadding(0, 30, 0, 30)
    }


    fun insertEditText(position: Int, text: CharSequence?): TextView {
        val nextHint = if (isLastText(position)) null else editorCore.placeHolder
        if (editorCore.renderType === RenderType.EDITOR) {

            /**
             * when user press enter from first line without keyin anything, need to remove the placeholder from that line 0...
             */
            if (position == 1) {
                val view = editorCore.parentView.getChildAt(0)
                val type = editorCore.getControlType(view)
                if (type === EditorType.INPUT) {
                    val textView = view as TextView
                    if (TextUtils.isEmpty(textView.text)) {
                        textView.hint = null
                    }
                }
            }

            val view = getNewEditTextInst(nextHint, text)
            editorCore.parentView.addView(view, position)
            editorCore.activeView = view
            val handler = Handler()
            handler.postDelayed({
                setFocus(view)
            }, 0)
            editorCore.activeView = view
            return view
        } else {
            val view = getNewTextView(text)
            editorCore.parentView.addView(view)
            return view
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun updateTextStyle(style: EditorTextStyle, editText: TextView?) {
        try {
            val editTextLocal = (editText ?: editor) as CustomEditText

            // Process the operation only if a selection area exists
            editTextLocal.selectionArea?.let { selection ->
                val spansWorker = SpansWorkerImpl(editTextLocal.text)

                val calculator = StyleSpansCalculator(spansWorker)
                val spanOperations = calculator.calculate(selection, style)

                spanOperations.forEach { operation ->
                    when(operation) {
                        is DeleteSpanOperation -> spansWorker.removeSpan(operation.span)

                        is CreateSpanOperation<*> -> {
                            with((operation as CreateSpanOperation<EditorTextStyle>).spanInfo) {
                                spansWorker.createSpan(StyleSpan(styleToTypeface(value)), area)
                            }
                        }
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun styleToTypeface(style: EditorTextStyle): Int =
        when(style) {
            EditorTextStyle.ITALIC -> Typeface.ITALIC
            EditorTextStyle.BOLD -> Typeface.BOLD
            EditorTextStyle.BOLD_ITALIC -> Typeface.BOLD_ITALIC
        }

    fun insertLink() {
        val inputAlert = AlertDialog.Builder(this.editorCore.context)
        inputAlert.setTitle("Add a Link")
        val userInput = EditText(this.editorCore.context)

        //don't forget to add some margins on the left and right to match the title
        userInput.hint = "type the URL here"
        userInput.inputType = InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT
        inputAlert.setView(userInput)

        inputAlert.setPositiveButton("INSERT") { _, _ ->
            val userInputValue = userInput.text.toString()
            insertLink(userInputValue)
        }
        inputAlert.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        val alertDialog = inputAlert.create()
        alertDialog.show()
    }

    fun insertLink(uri: String) {
        val editorType = editorCore.getControlType(editorCore.activeView)
        val editText = editorCore.activeView as EditText?

        if (editorType === EditorType.INPUT || editorType === EditorType.UL_LI) {
            var text = editText!!.text.toHtml()

            if (TextUtils.isEmpty(text)) {
                text = "<p dir=\"ltr\"></p>"
            }

            text = trimLineEnding(text)
            val doc = Jsoup.parse(text)
            val x = doc.select("p")
            val existing = x[0].html()
            x[0].html("$existing <a href='$uri'>$uri</a>")
            val toTrim = x.toString().fromHtml()
            val trimmed = noTrailingWhiteLines(toTrim)
            editText.setText(trimmed)   //
            editText.setSelection(editText.text.length)
        }
    }

    fun noTrailingWhiteLines(text: CharSequence): CharSequence {
        var editedText = text

        if (editedText.isEmpty()) {
            return editedText
        }

        while (editedText[editedText.length - 1] == '\n') {
            editedText = editedText.subSequence(0, editedText.length - 1)
        }

        return editedText
    }

    fun isEditTextEmpty(editText: EditText): Boolean = editText.text.toString().trim { it <= ' ' }.isEmpty()

    private fun trimLineEnding(s: String): String {
        return if (s[s.length - 1] == '\n') {
            s.substring(0, s.length - 1)
        } else s
    }

    private fun setFocus(view: CustomEditText) {
        if (editorCore.isStateFresh && !editorCore.autoFocus) {
            editorCore.isStateFresh = false
            return
        }
        view.requestFocus()
        val mgr = editorCore.activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        view.setSelection(view.text?.length ?: 0)
        editorCore.activeView = view
    }


    fun getEditTextPrevious(startIndex: Int): CustomEditText? {
        var customEditText: CustomEditText? = null
        for (i in 0 until startIndex) {
            val view = editorCore.parentView.getChildAt(i)
            val editorType = editorCore.getControlType(view)
            if (editorType === EditorType.EMBED || editorType === EditorType.MAP)
                continue
            if (editorType === EditorType.INPUT) {
                customEditText = view as CustomEditText
                continue
            }
        }
        return customEditText
    }

    fun setFocusToPrevious(startIndex: Int) {
        for (i in startIndex downTo 1) {
            val view = editorCore.parentView.getChildAt(i)
            val editorType = editorCore.getControlType(view)
            if (editorType === EditorType.EMBED || editorType === EditorType.MAP)
                continue
            if (editorType === EditorType.INPUT) {
                setFocus(view as CustomEditText)
                break
            }
        }
    }

    fun isInputTextAtPosition(position: Int): Boolean {
        return editorCore.getControlType(editorCore.parentView.getChildAt(position)) === EditorType.INPUT
    }

    fun applyStyles(editText: TextView, element: Element) {
//        val styles = componentsWrapper!!.htmlExtensions!!.getStyleMap(element)
//        if (styles.containsKey("color")) {
//            updateTextColor(MaterialColor.BLACK, editText)
//        }
    }

    fun getInputHtml(): String = ""

    @Suppress("UNUSED_PARAMETER")
    fun applyTextSettings(node: Node, view: TextView) {
    }

    fun removeFocus(editText: CustomEditText) {
        editText.clearFocus()
        val imm = editorCore.activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
        editorCore.parentView.removeView(editText)
    }

    fun setLineSpacing(lineSpacing: Float) {
        this.lineSpacing = lineSpacing
    }

    private fun getNewTextView(text: CharSequence?): TextView {
        val textView = TextView(ContextThemeWrapper(this.editorCore.context, R.style.WysiwygEditText))
        addEditableStyling(textView)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        textView.layoutParams = params

        if (!TextUtils.isEmpty(text)) {
            val toReplace = noTrailingWhiteLines(text.toString().fromHtml())
            textView.text = toReplace
            Linkify.addLinks(textView, Linkify.ALL)
        }

        if (this.lineSpacing != -1f) {
            setLineSpacing(textView, this.lineSpacing)
        }
        return textView
    }

    private fun insertTag(tagText: String, addSpace: Boolean, tryToMoveCursor: Boolean) =
        insertSpecialSpan("#$tagText", TagSpan(tagText, specialSpansColors), addSpace, tryToMoveCursor)

    private fun insertMention(userName: String, addSpace: Boolean, tryToMoveCursor: Boolean) =
        insertSpecialSpan("@$userName", MentionSpan(userName, specialSpansColors), addSpace, tryToMoveCursor)

    private fun insertLinkInText(linkInfo: LinkInfo, addSpace: Boolean, tryToMoveCursor: Boolean) =
        insertSpecialSpan(linkInfo.text!!, LinkSpan(linkInfo, specialSpansColors), addSpace, tryToMoveCursor)

    /**
     * Inserts special span into a cursor position
     */
    private fun insertSpecialSpan(textToDisplay: String, span: CharacterStyle, addSpace: Boolean, tryToMoveCursor: Boolean) {
        try {
            if(editor.selectionArea != null) {
                return
            }

            editor.text?.let { textArea ->
                val spansWorker = SpansWorkerImpl(textArea)

                val startPosition = editor.cursorPosition
                val endPosition = editor.cursorPosition+textToDisplay.length

                val suffix = if(addSpace) " " else ""

                textArea.insert(editor.cursorPosition, "$textToDisplay$suffix")

                editor.post {
                    spansWorker.createSpan(span, startPosition..endPosition)
                    if(tryToMoveCursor && textArea.lastIndex >= endPosition) {
                        editor.setCursorPosition(endPosition+1)
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /**
     * Removes special span under a cursor position
     */
    private fun removeSpecialSpan(spanType: KClass<*>): Boolean {
        try {
            if(editor.selectionArea != null) {
                return false
            }

            editor.text?.let { textArea ->
                val spansWorker = SpansWorkerImpl(textArea)

                spansWorker.getSpanUnderPosition(spanType, editor.cursorPosition)
                    ?.let { span ->
                        val spanInterval = spansWorker.getSpanInterval(span)

                        spansWorker.removeSpan(span)

                        editor.post {
                            textArea.delete(spanInterval.first, spanInterval.last)
                        }

                        return true
                    }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return false
    }

    /**
     * Get data of a special span under a cursor
     * @return a pair with display text and value (or null if a span is not found)
     */
    private fun <T>getSpecialSpanData(spanType: KClass<*>, getValueAction: (CharacterStyle) -> T): Pair<String, T>? {
        try {
            if(editor.selectionArea != null) {
                return null
            }

            editor.text?.let { textArea ->
                val spansWorker = SpansWorkerImpl(textArea)

                spansWorker.getSpanUnderPosition(spanType, editor.cursorPosition)
                    ?.let { span ->
                        val spanInterval = spansWorker.getSpanInterval(span)

                        val displayText = textArea.subSequence(spanInterval.first, spanInterval.last).toString()
                        val value = getValueAction(span)

                        return Pair(displayText, value)
                    }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return null
    }

    private fun getSanitizedHtml(text: CharSequence): CharSequence = noTrailingWhiteLines(text.toString().fromHtml())

    private fun CharSequence.isEnterPressed(): Boolean {
        for(i in this.indices) {
            if(this[i] == '\n') {
                return true
            }
        }
        return false
    }
}
