package io.golos.cyber_android.ui.screens.wallet_shared.history.data_source

import io.golos.cyber_android.ui.shared.recycler_view.versioned.paging.LoadedItemsList

interface HistoryDataSource : LoadedItemsList {
    var communityId: String
}