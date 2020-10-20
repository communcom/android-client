package io.golos.cyber_android.ui.shared.extensions

import android.content.ContextWrapper
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 * Return parent activity for a view
 */
val View.parentActivity: AppCompatActivity?
    get() {
        var context = this.context
        while (context is ContextWrapper) {
            if (context is AppCompatActivity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }

fun View.setPadding(padding: Int) = this.setPadding(padding, padding, padding, padding)