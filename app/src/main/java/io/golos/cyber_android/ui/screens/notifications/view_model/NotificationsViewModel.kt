package io.golos.cyber_android.ui.screens.notifications.view_model

import io.golos.cyber_android.ui.screens.notifications.model.NotificationsModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class NotificationsViewModel @Inject constructor(notificationsModel: NotificationsModel,
                                                 dispatchersProvider: DispatchersProvider,
                                                 private val paginator: Paginator.Store<VersionedListItem>) : ViewModelBase<NotificationsModel>(dispatchersProvider, notificationsModel) {
}