package io.golos.cyber_android.ui.screens.wallet_point.dto

import io.golos.cyber_android.ui.screens.wallet_shared.carousel.CarouselListItem

data class CarouselStartData(
    val startIndex: Int,
    val items: List<CarouselListItem>
)