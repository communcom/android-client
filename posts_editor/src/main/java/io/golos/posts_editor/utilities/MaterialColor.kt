package io.golos.posts_editor.utilities

import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import io.golos.posts_editor.R
import kotlin.math.pow
import kotlin.math.sqrt

@Suppress("unused")
enum class MaterialColor(@ColorRes val value: Int) {
    BLACK(R.color.black),
    WHITE(R.color.white),
    RED(R.color.red),
    PINK(R.color.pink),
    PURPLE(R.color.purple),
    DEEP_PURPLE(R.color.deepPurple),
    INDIGO(R.color.indigo),
    BLUE(R.color.blue),
    LIGHT_BLUE(R.color.lightBlue),
    CYAN(R.color.cyan),
    TEAL(R.color.teal),
    GREEN(R.color.green),
    LIGHT_GREEN(R.color.lightGreen),
    LIME(R.color.lime),
    YELLOW(R.color.yellow),
    AMBER(R.color.amber),
    ORANGE(R.color.orange),
    DEEP_ORANGE(R.color.deepOrange);

    companion object Create {
        @ColorInt
        fun toSystemColor(color: MaterialColor, context: Context): Int = getColor(color.value, context)

        fun fromSystemColor(@ColorInt color: Int, context: Context): MaterialColor {
            var minDistance = Double.MAX_VALUE
            lateinit var minValueColor: MaterialColor

            values().forEach { materialColor ->
                val distance = getDistance(toSystemColor(materialColor, context), color)
                if(distance < minDistance) {
                    minDistance = distance
                    minValueColor = materialColor
                }
            }

            return minValueColor
        }

        @Suppress("DEPRECATION")
        @ColorInt
        private fun getColor(@ColorRes resId: Int, context: Context): Int =
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.resources.getColor(resId, null)
            } else {
                context.resources.getColor(resId)
            }

        private fun getDistance(@ColorInt color1: Int, @ColorInt color2: Int): Double =
            ((Color.red(color1) - Color.red(color2)).toDouble().pow(2) +
            (Color.green(color1) - Color.green(color2)).toDouble().pow(2) +
            (Color.blue(color1) - Color.blue(color2)).toDouble().pow(2))
                .let { sqrt(it) }
    }
}