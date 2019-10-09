package io.golos.cyber_android.ui.common.extensions

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController

fun NavController.safeNavigate(@IdRes from: Int, @IdRes action: Int, args: Bundle? = null) {
    if (this.currentDestination?.id == from)
        this.navigate(action, args)
}


