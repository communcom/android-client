package io.golos.cyber_android.ui.screens.profile_bio.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import javax.inject.Inject

class ProfileBioModelImpl
@Inject
constructor(
) : ModelBaseImpl(),
    ProfileBioModel {

    override val maxTextLen: Int = 100
}