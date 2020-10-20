package io.golos.domain.dto

import io.golos.commun4j.services.model.TransferHistoryDirection
import io.golos.commun4j.services.model.TransferHistoryDonation
import io.golos.commun4j.services.model.TransferHistoryHoldType
import io.golos.commun4j.services.model.TransferHistoryTransferType

data class HistoryFilterDomain(
    val transferType:TransferHistoryTransferType= TransferHistoryTransferType.ALL,
    val holdType: TransferHistoryHoldType? = TransferHistoryHoldType.ALL,
    val direction: TransferHistoryDirection? = TransferHistoryDirection.ALL,
    val reward:String? = "all",
    val claim:TransferHistoryDonation? = TransferHistoryDonation.ALL,
    val donation:TransferHistoryDonation? = TransferHistoryDonation.ALL
)