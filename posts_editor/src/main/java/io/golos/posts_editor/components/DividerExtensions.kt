package com.github.irshulx.components

import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

import io.golos.posts_editor.EditorComponent
import io.golos.posts_editor.EditorCore
import io.golos.posts_editor.R
import io.golos.posts_editor.components.ComponentsWrapper
import io.golos.posts_editor.components.input.edit_text.CustomEditText
import io.golos.posts_editor.models.EditorContent
import io.golos.posts_editor.models.EditorType
import io.golos.posts_editor.models.Node
import io.golos.posts_editor.models.RenderType
import io.golos.posts_editor.models.control_metadata.ImageMetadata
import org.jsoup.nodes.Element

class DividerExtensions(
    private var editorCore: EditorCore
) : EditorComponent(editorCore) {
    private var dividerLayout = R.layout.widget_divider

    override fun getContent(view: View): Node {
        return getNodeInstance(view)
    }

    override fun getContentAsHTML(node: Node, content: EditorContent): String {
        return componentsWrapper!!.htmlExtensions!!.getTemplateHtml(EditorType.HR)
    }

    override fun renderEditorFromState(node: Node, content: EditorContent) {
        insertDivider(content.nodes!!.indexOf(node))
    }

    override fun buildNodeFromHTML(element: Element): Node? {
        val count = editorCore.childCount
        insertDivider(count)
        return null
    }

    override fun init(componentsWrapper: ComponentsWrapper) {
        this.componentsWrapper = componentsWrapper
    }

    fun setDividerLayout(layout: Int) {
        this.dividerLayout = layout
    }

    fun insertDivider(index: Int) {
        var determinateIndex = index

        val view = (editorCore.context as Activity).layoutInflater.inflate(this.dividerLayout, null)
        view.tag = ImageMetadata(EditorType.HR)

        if (determinateIndex == -1) {
            determinateIndex = editorCore.determineIndex(EditorType.HR)
        }
        if (determinateIndex == 0) {
            Toast.makeText(editorCore.context, "divider cannot be inserted on line zero", Toast.LENGTH_SHORT).show()
            return
        }
        editorCore.parentView!!.addView(view, determinateIndex)

        if (editorCore.renderType === RenderType.EDITOR) {

            if (editorCore.getControlType(editorCore.parentView!!.getChildAt(determinateIndex + 1)) === EditorType.INPUT) {
                val customEditText = editorCore.getChildAt(determinateIndex + 1) as CustomEditText
                componentsWrapper!!.inputExtensions!!.removeFocus(customEditText)
            }

            view.setOnTouchListener(View.OnTouchListener { touchView, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val paddingTop = touchView.paddingTop
                    val paddingBottom = touchView.paddingBottom
                    val height = touchView.height
                    if (event.y < paddingTop) {
                        editorCore.onViewTouched(0, editorCore.parentView!!.indexOfChild(touchView))
                    } else if (event.y > height - paddingBottom) {
                        editorCore.onViewTouched(1, editorCore.parentView!!.indexOfChild(touchView))
                    }
                    return@OnTouchListener false
                }
                true
            })

            val focus = editorCore.activity.currentFocus
            if (focus != null) {
                val imm = editorCore.activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)

                if (focus is CustomEditText) {
                    focus.clearFocus()
                    editorCore.parentView!!.requestFocus()
                }
            }

        }
    }

    fun removeAllDividersBetweenDeletedAndFocusNext(indexOfDeleteItem: Int, nextFocusIndex: Int) {
        for (i in nextFocusIndex until indexOfDeleteItem) {
            if (editorCore.getControlType(editorCore.parentView!!.getChildAt(i)) === EditorType.HR) {
                editorCore.parentView!!.removeViewAt(i)
            }
        }
    }
}