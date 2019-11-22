package io.golos.cyber_android.ui.screens.profile_photos.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class ProfilePhotosModelImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider
) : ModelBaseImpl(),
    ProfilePhotosModel {
}