package io.golos.data.mappers

import io.golos.commun4j.services.model.DonationItem
import io.golos.domain.dto.DonationsDomain
import io.golos.domain.dto.DonatorDomain

fun DonationItem.mapToDonationsDomain() =
    DonationsDomain(
        contentId = this.contentId!!.mapToContentIdDomain(),
        totalAmount = totalAmount!!,
        donators =  this.donations?.map { DonatorDomain(it.sender!!.mapToUserBriefDomain(), it.quantity) } ?: listOf()
    )