package io.golos.cyber_android.ui.dto

import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.DonationsDomain

data class PostDonation(
    val postId: ContentIdDomain,
    val donation: DonationsDomain
)