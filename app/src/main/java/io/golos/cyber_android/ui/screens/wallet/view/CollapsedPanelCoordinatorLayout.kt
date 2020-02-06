package io.golos.cyber_android.ui.screens.wallet.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import io.golos.cyber_android.ui.shared.extensions.getStatusBarHeight
import kotlin.math.abs

class CollapsedPanelCoordinatorLayout
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr) {

    private companion object {
        const val PRIME_PANEL_TAG = "PRIME_PANEL"
        const val COLLAPSED_PANEL_TAG = "COLLAPSED_PANEL"
        const val COLLAPSED_PANEL_CONTENT_TAG = "COLLAPSED_PANEL_CONTENT"
        const val COLLAPSING_TOOLBAR_TAG = "COLLAPSING_TOOLBAR"
        const val APPBAR_TAG = "APPBAR"
    }

    private lateinit var collapsedPanel: View
    private var collapsedPanelThreshold = 0

    private var collapsingToolbarHeight = 0

    private var maxCollapsingOffset = 0

    override fun onLayoutChild(child: View, layoutDirection: Int) {
        super.onLayoutChild(child, layoutDirection)

        // Setting up the collapsed panel visibility threshold
        val statusBarHeight = resources.getStatusBarHeight()

        val collapsingToolbar = findViewWithTag<CollapsingToolbarLayout>(COLLAPSING_TOOLBAR_TAG)
        collapsedPanelThreshold = collapsedPanel.height + statusBarHeight + resources.displayMetrics.heightPixels/20
        collapsingToolbar.scrimVisibleHeightTrigger = collapsedPanelThreshold
        collapsingToolbar.scrimAnimationDuration = context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        collapsingToolbarHeight = collapsingToolbar.height
        maxCollapsingOffset = -(collapsingToolbarHeight - collapsedPanel.height - statusBarHeight)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val statusBarHeight = resources.getStatusBarHeight()

        // Setting of a top margin for the panels
        val primePanel = findViewWithTag<View>(PRIME_PANEL_TAG)
        collapsedPanel = findViewWithTag<View>(COLLAPSED_PANEL_TAG)

        setMarginForPanel(primePanel, statusBarHeight)
        setMarginForPanel(collapsedPanel, statusBarHeight)

        // Setting up collapsed panel content animation
        val appbar = findViewWithTag<AppBarLayout>(APPBAR_TAG)
        val collapsedPanelContent = findViewWithTag<View>(COLLAPSED_PANEL_CONTENT_TAG)

        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBar, offset ->
            val startCollapsingOffset = (collapsedPanelThreshold-collapsingToolbarHeight).toFloat()

            collapsedPanelContent.alpha = if(offset < startCollapsingOffset) {
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