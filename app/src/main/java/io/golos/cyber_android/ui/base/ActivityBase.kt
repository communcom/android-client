package io.golos.cyber_android.ui.base

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

abstract class ActivityBase: AppCompatActivity() {
    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}