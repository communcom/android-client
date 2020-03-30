package io.golos.cyber_android.ui.screens.app_start.sign_in.activity

import android.os.Bundle
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.app_start.sign_in.activity.di.SignInActivityComponent
import io.golos.cyber_android.ui.shared.base.ActivityBase

class SignInActivity : ActivityBase() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        overridePendingTransition(R.anim.nav_slide_in_right, R.anim.nav_slide_out_left)
    }

    override fun inject(key: String) = App.injections.get<SignInActivityComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignInActivityComponent>(key)
}
