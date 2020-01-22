package io.golos.cyber_android.ui.shared.countries

import io.golos.domain.dto.CountryDomain

data class CountryEntity (
    val country: CountryDomain,
    val searchName: String
)