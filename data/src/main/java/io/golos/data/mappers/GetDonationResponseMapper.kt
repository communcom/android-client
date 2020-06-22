package io.golos.data.mappers

import io.golos.commun4j.services.model.DonationItem
import io.golos.domain.dto.DonationsDomain

fun DonationItem.mapToDonationsDomain() =
    DonationsDomain(
        contentId = this.contentId!!.mapToContentIdDomain(),
        totalAmount = totalAmount!!
    )