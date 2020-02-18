package io.golos.cyber_android.ui.screens.wallet_send_points.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.UserDomain

interface WalletSendPointsModel : ModelBase {
    var sendToUser: UserDomain?
}