package io.golos.cyber_android.ui.screens.wallet_send_points.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.domain.dto.UserDomain
import javax.inject.Inject

class WalletSendPointsModelImpl
@Inject
constructor(
    override var sendToUser: UserDomain?
) : ModelBaseImpl(), WalletSendPointsModel {
}