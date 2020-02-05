package io.golos.cyber_android.ui.screens.wallet.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import io.golos.cyber_android.ui.shared.extensions.getStatusBarHeight

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
    }

    private lateinit var collapsedPanel: View

    override fun onLayoutChild(child: View, layoutDirection: Int) {
        super.onLayoutChild(child, layoutDirection)

        // Setting up the collapsed panel visibility threshold
        val collapsingToolbar = findViewWithTag<CollapsingToolbarLayout>(COLLAPSING_TOOLBAR_TAG)
        collapsingToolbar.scrimVisibleHeightTrigger =
            collapsedPanel.height + resources.getStatusBarHeight() + resources.displayMetrics.heightPixels/20
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val statusBarHeight = resources.getStatusBarHeight()

        // Setting of a top margin for the panels
        val primePanel = findViewWithTag<View>(PRIME_PANEL_TAG)
        collapsedPanel = findViewWithTag<View>(COLLAPSED_PANEL_TAG)

        setMarginForPanel(primePanel, statusBarHeight)
        setMarginForPanel(collapsedPanel, statusBarHeight)
    }

    private fun setMarginForPanel(panel: View, statusBarHeight: Int) {
        val lParams = panel.layoutParams as MarginLayoutParams
        lParams.topMargin = statusBarHeight
        panel.layoutParams = lParams
    }
}

// Show COLLAPSED_PANEL_CONTENT_TAG animated as same as scrim animation (see WalletFragment::onViewCreated)