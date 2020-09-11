package io.golos.domain.dto

import io.golos.commun4j.services.model.TransferHistoryDirection
import io.golos.commun4j.services.model.TransferHistoryDonation
import io.golos.commun4j.services.model.TransferHistoryHoldType
import io.golos.commun4j.services.model.TransferHistoryTransferType

data class HistoryFilterDomain(
    val transferType:TransferHistoryTransferType? = null,
    val holdType: TransferHistoryHoldType? = null,
    val direction: TransferHistoryDirection? = null,
    val reward:String? = null,
    val claim:TransferHistoryDonation? = null,
    val donation:TransferHistoryDonation? = null
)