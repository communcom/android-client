package io.golos.cyber_android.ui.screens.edit_profile_bio_activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.base.ActivityBase
import io.golos.cyber_android.ui.shared_fragments.bio.EditProfileBioFragment

class EditProfileBioActivity : ActivityBase() {

    companion object {
        fun getIntent(context: Context, args: EditProfileBioFragment.Args) =
            Intent(context, EditProfileBioActivity::class.java)
                .apply { putExtra(Tags.ARGS, args) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_bio_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container,
                    EditProfileBioFragment.newInstance(intent.getStringExtra(Tags.ARGS))
                )
                .commitNow()
        }
    }

}
