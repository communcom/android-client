package io.golos.cyber_android.application.dependency_injection.graph.app.ui.dialogs.select_community_dialog

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.screens.main_activity.communities.data_repository.dto.CommunityType

@Module
class SelectCommunityDialogModule {
    @Provides
    internal fun provideCommunityType(): CommunityType = CommunityType.USER
}