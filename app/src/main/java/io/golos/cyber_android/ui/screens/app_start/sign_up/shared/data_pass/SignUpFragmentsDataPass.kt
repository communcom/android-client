package io.golos.cyber_android.ui.screens.app_start.sign_up.shared.data_pass

import io.golos.domain.dto.CountryDomain

interface SignUpFragmentsDataPass {
    fun putSelectedCountry(country: CountryDomain)
    fun getSelectedCountry(): CountryDomain?
}