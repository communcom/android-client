package io.golos.cyber_android.ui.shared.widgets.pin

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.AttrRes
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.di.UIComponent
import io.golos.cyber_android.ui.shared.characters.SpecialChars
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class PinDigit
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : TextView(context, attrs, defStyleAttr), CoroutineScope  {

    private val passwordChar = SpecialChars.BLACK_CIRCLE

    private val digitVisibilityDelay = 500L      // ms

    private val emptyTextSize = context.resources.getDimension(R.dimen.text_size_12_sp)
    private val fillTextSize = context.resources.getDimension(R.dimen.text_size_12_sp)

    enum class DrawableState(@AttrRes val resId: Int) {
        EMPTY(R.attr.state_empty),
        FILL(R.attr.state_fill),
    }

    private var currentDrawableState: DrawableState? = DrawableState.EMPTY

    @Inject
    internal lateinit var dispatchersProvider: DispatchersProvider

    init {
        App.injections.get<UIComponent>().inject(this)
    }

    var digit: Digit? = null
    set(value) {
        field = value
        startDigitVisualization(value)
    }

    private val parentJob = SupervisorJob()
    private var childJob: Job? = null

    override val coroutineContext: CoroutineContext
        get() = parentJob + dispatchersProvider.uiDispatcher

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

        val textValue = digit?.value?.toString() ?: passwordChar
        textSize = if(textValue == passwordChar) emptyTextSize else fillTextSize
        text = textValue

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
            textSize = emptyTextSize
        }
    }
}
