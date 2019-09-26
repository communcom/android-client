package io.golos.posts_editor.components

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.util.Linkify
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import io.golos.posts_editor.EditorComponent
import io.golos.posts_editor.EditorCore
import io.golos.posts_editor.R
import io.golos.posts_editor.components.input.edit_text.CustomEditText
import io.golos.domain.post_editor.EmbedType
import io.golos.posts_editor.models.*
import io.golos.domain.post_editor.EmbedMetadata
import org.jsoup.nodes.Element
import java.util.*

class EmbedExtensions(private val editorCore: EditorCore) : EditorComponent<EmbedMetadata>(editorCore) {
    private var requestListener: RequestListener<Drawable>? = null
    private var requestOptions: RequestOptions? = null
    private var transition: DrawableTransitionOptions? = null

    /**
     * @param the value is true if some embed was added
     */
    private var onEmbedAddedOrRemovedListener: ((Boolean) -> Unit)? = null

    @DrawableRes
    var placeholder = -1
    @DrawableRes
    var errorBackground = -1

    override fun getContent(view: View): Node {
        val node = getNodeInstance(view)
        val imgTag = view.tag as EmbedMetadata

        node.content!!.add(imgTag.displayUri.toString())

        // for subtitle
        val textView = getDescription(view)

        val subTitleNode = getNodeInstance(textView)

        node.childs = ArrayList()
        node.childs!!.add(subTitleNode)

        return node
    }

    override fun getMetadata(view: View): EmbedMetadata? =
        (view.tag as? EmbedMetadata)?.copy(description = getDescription(view).text.toString())

    override fun getContentAsHTML(node: Node, content: EditorContent): String = ""

    override fun renderEditorFromState(node: Node, content: EditorContent) { }

    override fun buildNodeFromHTML(element: Element): Node?  = null

    override fun init(componentsWrapper: ComponentsWrapper) {
        this.componentsWrapper = componentsWrapper
    }

    fun setOnEmbedAddedOrRemovedListener(listener: ((Boolean) -> Unit)?) {
        this.onEmbedAddedOrRemovedListener = listener
    }

    fun openImageGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        (editorCore.context as Activity).startActivityForResult(Intent.createChooser(intent, "Select an image"), EditorCore.PICK_IMAGE_REQUEST)
    }

    @SuppressLint("InflateParams")
    fun insert(type: EmbedType, sourceUri: Uri, displayUri: Uri, description: String?) {
        // Render(getStateFromString());
        val childLayout = EmbedWidget(editorCore.context)
        val imageView = childLayout.findViewById<ImageView>(R.id.imageView)

        val desc = getDescription(childLayout)

        loadImageUsingLib(displayUri, imageView)

        // Index of embed in a list of controls
        var embedIndex = editorCore.determineIndex(EditorType.EMBED)

        // Remove a prior paragraph if the paragraph is empty
        val priorView = editorCore.parentView.getChildAt(embedIndex-1)
        if(priorView is CustomEditText && priorView.text.isNullOrEmpty()) {
            editorCore.parentView.removeViewAt(embedIndex-1)
            embedIndex--
        }

        // Insert the embed
        showNextInputHint(embedIndex)
        editorCore.parentView.addView(childLayout, embedIndex)

        childLayout.tag = EmbedMetadata(type, sourceUri, displayUri, description)

        desc.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                desc.clearFocus()
            } else {
                editorCore.activeView = desc
            }
        }

        // Put a new paragraph under the embed (for long read editor only)
        if (editorCore.isLastRow(childLayout) && !editorCore.isSimpleEditor) {
            componentsWrapper!!.inputExtensions!!.insertEditText(embedIndex + 1, null)
        }

        if (!description.isNullOrEmpty()) {
            componentsWrapper!!.inputExtensions!!.setText(desc, description)
        }

        if (editorCore.renderType === RenderType.EDITOR) {
            bindEvents(childLayout)
        } else {
            desc.isEnabled = false
        }

        onEmbedAddedOrRemovedListener?.invoke(true)
    }

    private fun getDescription(view: View): CustomEditText = view.findViewById(R.id.descriptionText)

    private fun showNextInputHint(index: Int) {
//        val view = editorCore.parentView.getChildAt(index)
//        val type = editorCore.getControlType(view)
//        if (type !== EditorType.INPUT)
//            return
//        val tv = view as TextView
//        tv.hint = editorCore.placeHolder
//        Linkify.addLinks(tv, Linkify.ALL)
    }

    private fun hideInputHint(index: Int) {
        val view = editorCore.parentView.getChildAt(index)
        val type = editorCore.getControlType(view)
        if (type !== EditorType.INPUT)
            return

        var hint = editorCore.placeHolder
        if (index > 0) {
            val prevView = editorCore.parentView.getChildAt(index - 1)
            val prevType = editorCore.getControlType(prevView)
            if (prevType === EditorType.INPUT)
                hint = null
        }
        val tv = view as TextView
        tv.hint = hint
    }

    @SuppressLint("CheckResult")
    fun loadImageUsingLib(uri: Uri, imageView: ImageView) {
        if (requestListener == null) {
            requestListener = object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    return false
                }
            }
        }

        if (placeholder == -1) {
            placeholder = R.drawable.image_placeholder
        }

        if (errorBackground == -1) {
            errorBackground = R.drawable.error_background
        }

        if (requestOptions == null) {
            requestOptions = RequestOptions()
        }

        requestOptions!!.placeholder(placeholder)
        requestOptions!!.error(errorBackground)

        if (transition == null) {
            transition = DrawableTransitionOptions.withCrossFade().crossFade(1000)
        }
        Glide.with(imageView.context)
                .load(uri)
                .transition(transition!!)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)   //No disk cache
                .listener(requestListener)
                .apply(requestOptions!!)
                .into(imageView)
    }

    private fun bindEvents(layout: View) {
        val imageView = layout.findViewById<ImageView>(R.id.imageView)
        val btnRemove = layout.findViewById<View>(R.id.btn_remove)

        btnRemove.setOnClickListener {
            val index = editorCore.parentView.indexOfChild(layout)
            editorCore.parentView.removeView(layout)
            hideInputHint(index)
            componentsWrapper!!.inputExtensions!!.setFocusToPrevious(index)

            onEmbedAddedOrRemovedListener?.invoke(false)
        }

        layout.setOnTouchListener(View.OnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val paddingTop = view.paddingTop
                val paddingBottom = view.paddingBottom
                val height = view.height

                when {
                    event.y < paddingTop -> editorCore.onViewTouched(0, editorCore.parentView.indexOfChild(layout))
                    event.y > height - paddingBottom -> editorCore.onViewTouched(1, editorCore.parentView.indexOfChild(layout))
                }

                return@OnTouchListener false
            }
            true        //hmmmm....
        })

        imageView.setOnClickListener { btnRemove.visibility = View.VISIBLE }
        imageView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            btnRemove.visibility = if (hasFocus) View.VISIBLE else View.GONE
        }
    }
}