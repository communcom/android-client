package io.golos.domain.dto

import io.golos.commun4j.services.model.TransferHistoryDirection
import io.golos.commun4j.services.model.TransferHistoryHoldType
import io.golos.commun4j.services.model.TransferHistoryTransferType

data class HistoryFilterDomain(
    val transferType:TransferHistoryTransferType,
    val holdType: TransferHistoryHoldType,
    val direction: TransferHistoryDirection,
    val reward:String
)