package io.golos.cyber_android.ui.screens.login_activity.signup.pin.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.AttrRes
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class PinDigit
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : TextView(context, attrs, defStyleAttr), CoroutineScope  {

    private val passwordChar = intArrayOf(0x25CF).let { String(it, 0, it.size) }    // Large dot

    private val digitVisibilityDelay = 500L      // ms

    enum class DrawableState(@AttrRes val resId: Int) {
        NORMAL(R.attr.state_normal),
        ACTIVE(R.attr.state_active),
        NORMAL_ERROR(R.attr.state_normal_error),
        ACTIVE_ERROR(R.attr.state_active_error)
    }

    private var currentDrawableState: DrawableState? = DrawableState.NORMAL

    var digit: Digit? = null
    set(value) {
        field = value
        startDigitVisualization(value)
    }

    private val parentJob = SupervisorJob()
    private var childJob: Job? = null

    override val coroutineContext: CoroutineContext
        get() = parentJob + context.serviceLocator.dispatchersProvider.uiDispatcher

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        parentJob.takeIf { it.isActive }?.cancel()
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray =
        if(currentDrawableState == null) {
            super.onCreateDrawableState(extraSpace)
        } else {
            val drawableState = super.onCreateDrawableState(extraSpace + 1)

            mergeDrawableStates(drawableState, intArrayOf(currentDrawableState!!.resId))
            drawableState
        }

    fun hideDigit() {
        childJob?.takeIf { it.isActive }?.cancel()
    }

    fun setDrawableState(state: DrawableState) {
        currentDrawableState = state
        refreshDrawableState()
    }

    private fun startDigitVisualization(digit: Digit?) {
        childJob?.takeIf { it.isActive }?.cancel()

        text = digit?.value?.toString() ?: ""

        if(digit == null) {
            return                  // Reset case
        }

        childJob = launch {
            try {
                delay(digitVisibilityDelay)
                text = passwordChar
            } catch (ex: CancellationException) {
                if(parentJob.isActive) {
                    text = passwordChar
                }
            }
        }
    }
}
