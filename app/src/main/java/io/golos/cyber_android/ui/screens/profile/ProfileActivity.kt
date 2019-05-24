package io.golos.cyber_android.ui.screens.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.base.BaseActivity

class ProfileActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context, userId: String) =
            Intent(context, ProfileActivity::class.java).apply {
                putExtra(
                    Tags.USER_ID,
                    userId
                )
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        findNavController(R.id.profileNavHost).setGraph(R.navigation.graph_profile, intent.extras)
    }
}
