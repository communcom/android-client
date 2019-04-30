package io.golos.cyber_android.ui.screens.login.signup.country


import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.base.LoadingFragment
import io.golos.cyber_android.ui.screens.login.signup.SignUpViewModel
import io.golos.cyber_android.views.utils.BaseTextWatcher
import io.golos.domain.interactors.model.CountryModel
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.synthetic.main.fragment_sign_up_country.*
import kotlinx.android.synthetic.main.view_search_bar.*

/**
 * Fragment for selecting country (as [CountryModel]) for SignUpPhoneFragment
 */
class SignUpCountryFragment : LoadingFragment() {

    private lateinit var viewModel: SignUpCountryViewModel

    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up_country, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ViewCompat.setElevation(searchBar, resources.getDimension(R.dimen.elevation_feed_search_bar))
        val searchIcon = VectorDrawableCompat.create(resources, R.drawable.ic_search, null)
        searchBar.setCompoundDrawablesWithIntrinsicBounds(searchIcon, null, null, null)

        countriesList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        countriesList.adapter = CountriesAdapter(object : CountriesAdapter.Listener {
            override fun onCountryClick(country: CountryModel) {
                signUpViewModel.onCountrySelected(country)
                findNavController().navigateUp()
            }
        })
        searchBar.addTextChangedListener(object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.makeSearch(s.toString())
            }
        })

        back.setOnClickListener { findNavController().navigateUp() }

        setupViewModel()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.readinessLiveData.observe(this, Observer {
            when (it) {
                is QueryResult.Loading<*> -> showLoading()
                is QueryResult.Error<*> -> onLoadingError()
                is QueryResult.Success<*> -> onLoadingSuccess()
            }
        })

        viewModel.countriesLiveData.observe(this, Observer {
            (countriesList.adapter as CountriesAdapter).submit(it.list)
        })

        signUpViewModel.getSelectedCountryLiveData.observe(this, Observer {
            (countriesList.adapter as CountriesAdapter).selectedCountry = it
        })
    }

    private fun onLoadingSuccess() {
        hideLoading()
        viewModel.makeSearch("")
    }

    private fun onLoadingError() {
        hideLoading()
        Toast.makeText(requireContext(), "Loading error", Toast.LENGTH_SHORT).show()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            requireContext()
                .serviceLocator
                .getSignUpCountryViewModelFactory()
        ).get(SignUpCountryViewModel::class.java)

        signUpViewModel = ViewModelProviders.of(
            requireActivity(),
            requireContext()
                .serviceLocator
                .getSignUpViewModelFactory()
        ).get(SignUpViewModel::class.java)
    }
}
