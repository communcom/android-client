package io.golos.data.mappers

import io.golos.commun4j.services.model.GetDonationResponse
import io.golos.domain.dto.DonationsDomain

fun GetDonationResponse.mapToDonationsDomain() =
    DonationsDomain(
        contentId = this.contentId!!.mapToContentIdDomain(),
        totalAmount = totalAmount!!
    )