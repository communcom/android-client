package io.golos.cyber_android.ui.screens.wallet_shared

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import io.golos.cyber_android.R
import kotlin.math.abs

class CollapsedPanelCoordinatorLayout
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr) {

    @IdRes
    private var toolbarId: Int = 0
    @IdRes
    private var toolbarContentId: Int = 0
    @IdRes
    private var collapsingToolbarId: Int = 0
    @IdRes
    private var appbarId: Int = 0

    @Px
    private var toolbarHeight = 0f

    private lateinit var toolbar: View
    private var collapsedPanelThreshold = 0

    private var collapsingToolbarHeight = 0

    private var maxCollapsingOffset = 0

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CollapsedPanelCoordinatorLayout)

            toolbarId = typedArray.getResourceId(R.styleable.CollapsedPanelCoordinatorLayout_toolbar, 0)
            toolbarContentId = typedArray.getResourceId(R.styleable.CollapsedPanelCoordinatorLayout_toolbar_content, 0)
            collapsingToolbarId = typedArray.getResourceId(R.styleable.CollapsedPanelCoordinatorLayout_collapsing_toolbar, 0)
            appbarId = typedArray.getResourceId(R.styleable.CollapsedPanelCoordinatorLayout_appbar, 0)
            toolbarHeight = typedArray.getDimension(R.styleable.CollapsedPanelCoordinatorLayout_toolbar_height, 0f)

            typedArray.recycle()
        }
    }

    override fun onLayoutChild(child: View, layoutDirection: Int) {
        super.onLayoutChild(child, layoutDirection)

        // Setting up the collapsed panel visibility threshold
        val collapsingToolbar = findViewById<CollapsingToolbarLayout>(collapsingToolbarId)
        collapsedPanelThreshold = toolbar.height + resources.displayMetrics.heightPixels/20
        collapsingToolbar.scrimVisibleHeightTrigger = collapsedPanelThreshold
        collapsingToolbar.scrimAnimationDuration = context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        collapsingToolbarHeight = collapsingToolbar.height
        maxCollapsingOffset = -(collapsingToolbarHeight - toolbar.height)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        // Setting of a top margin for the panels
        toolbar = findViewById<View>(toolbarId)

        toolbar.translationY = -toolbarHeight

        // Setting up collapsed panel content animation
        val appbar = findViewById<AppBarLayout>(appbarId)

        val toolbarContent = findViewById<View>(toolbarContentId)

        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBar, offset ->
            val startCollapsingOffset = (collapsedPanelThreshold-collapsingToolbarHeight).toFloat()


            val translationFactor =  if(offset < startCollapsingOffset) {           // [0; 1]
                abs((startCollapsingOffset-offset) / (startCollapsingOffset - maxCollapsingOffset))
            } else {
                0f
            }

            toolbarContent.alpha = translationFactor
            toolbar.translationY = -toolbarHeight*(1f-translationFactor)

/*
            toolbarContent.alpha = if(offset < startCollapsingOffset) {
                abs((startCollapsingOffset-offset) / (startCollapsingOffset - maxCollapsingOffset))
            } else {
                0f
            }
*/

        })
    }
}