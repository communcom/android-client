package io.golos.cyber_android.ui.screens.profile.edit.bio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.base.ActivityBase

class EditProfileBioActivity : ActivityBase() {

    companion object {
        fun getIntent(context: Context, args: EditProfileBioFragment.Args) = Intent(context, EditProfileBioActivity::class.java).apply {
            putExtra(Tags.ARGS,
                context.serviceLocator.moshi.adapter(EditProfileBioFragment.Args::class.java)
                .toJson(args))
        }
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
