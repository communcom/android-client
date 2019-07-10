package io.golos.cyber_android.views.utils

import android.view.animation.Animation

/**
 * Simple [Animation.AnimationListener] that allows to implement only necessary methods
 */
open class BaseAnimationListener: Animation.AnimationListener {
    override fun onAnimationRepeat(p0: Animation?) {}

    override fun onAnimationEnd(p0: Animation?) { }

    override fun onAnimationStart(p0: Animation?) { }
}