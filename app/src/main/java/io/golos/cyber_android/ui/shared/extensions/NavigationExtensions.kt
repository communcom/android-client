package io.golos.cyber_android.ui.shared.extensions

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController

fun NavController.safeNavigate(@IdRes from: Int, @IdRes action: Int, args: Bundle? = null) {
    if (this.currentDestination?.id == from)
        this.navigate(action, args)
}


