package io.golos.cyber_android.ui.screens.app_start.sign_up.shared.data_pass

import io.golos.cyber_android.ui.shared.fragments_data_pass.FragmentsDataPassBase
import io.golos.domain.dependency_injection.scopes.ActivityScope
import io.golos.domain.dto.CountryDomain
import javax.inject.Inject

@ActivityScope
class SignUpFragmentsDataPassImpl
@Inject
constructor(): FragmentsDataPassBase(), SignUpFragmentsDataPass {
    private companion object {
        private const val SELECTED_COUNTRY_KEY = 907080
    }

    override fun putSelectedCountry(country: CountryDomain) = put(SELECTED_COUNTRY_KEY, country)

    override fun getSelectedCountry(): CountryDomain? = get(SELECTED_COUNTRY_KEY) as CountryDomain?
}