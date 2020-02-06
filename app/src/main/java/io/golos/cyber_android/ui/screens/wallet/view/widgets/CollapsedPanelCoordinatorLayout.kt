package io.golos.cyber_android.ui.screens.wallet.view.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IdRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.extensions.getStatusBarHeight
import kotlin.math.abs

class CollapsedPanelCoordinatorLayout
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr) {

    @IdRes
    private var primePanelId: Int = 0
    @IdRes
    private var toolbarId: Int = 0
    @IdRes
    private var toolbarContentId: Int = 0
    @IdRes
    private var collapsingToolbarId: Int = 0
    @IdRes
    private var appbarId: Int = 0

    private lateinit var toolbar: View
    private var collapsedPanelThreshold = 0

    private var collapsingToolbarHeight = 0

    private var maxCollapsingOffset = 0

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CollapsedPanelCoordinatorLayout)

            primePanelId = typedArray.getResourceId(R.styleable.CollapsedPanelCoordinatorLayout_prime_panel, 0)
            toolbarId = typedArray.getResourceId(R.styleable.CollapsedPanelCoordinatorLayout_toolbar, 0)
            toolbarContentId = typedArray.getResourceId(R.styleable.CollapsedPanelCoordinatorLayout_toolbar_content, 0)
            collapsingToolbarId = typedArray.getResourceId(R.styleable.CollapsedPanelCoordinatorLayout_collapsing_toolbar, 0)
            appbarId = typedArray.getResourceId(R.styleable.CollapsedPanelCoordinatorLayout_appbar, 0)

            typedArray.recycle()
        }
    }

    override fun onLayoutChild(child: View, layoutDirection: Int) {
        super.onLayoutChild(child, layoutDirection)

        // Setting up the collapsed panel visibility threshold
        val statusBarHeight = resources.getStatusBarHeight()

        val collapsingToolbar = findViewById<CollapsingToolbarLayout>(collapsingToolbarId)
        collapsedPanelThreshold = toolbar.height + statusBarHeight + resources.displayMetrics.heightPixels/20
        collapsingToolbar.scrimVisibleHeightTrigger = collapsedPanelThreshold
        collapsingToolbar.scrimAnimationDuration = context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        collapsingToolbarHeight = collapsingToolbar.height
        maxCollapsingOffset = -(collapsingToolbarHeight - toolbar.height - statusBarHeight)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val statusBarHeight = resources.getStatusBarHeight()

        // Setting of a top margin for the panels
        val primePanel = findViewById<View>(primePanelId)

        toolbar = findViewById<View>(toolbarId)
        toolbar.isClickable = true

        setMarginForPanel(primePanel, statusBarHeight)
        setMarginForPanel(toolbar, statusBarHeight)

        // Setting up collapsed panel content animation
        val appbar = findViewById<AppBarLayout>(appbarId)

        val toolbarContent = findViewById<View>(toolbarContentId)
        toolbarContent.isClickable = true

        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBar, offset ->
            val startCollapsingOffset = (collapsedPanelThreshold-collapsingToolbarHeight).toFloat()

            toolbarContent.alpha = if(offset < startCollapsingOffset) {
                abs((startCollapsingOffset-offset) / (startCollapsingOffset - maxCollapsingOffset))
            } else {
                0f
            }
        })
    }

    private fun setMarginForPanel(panel: View, statusBarHeight: Int) {
        val lParams = panel.layoutParams as MarginLayoutParams
        lParams.topMargin = statusBarHeight
        panel.layoutParams = lParams
    }
}