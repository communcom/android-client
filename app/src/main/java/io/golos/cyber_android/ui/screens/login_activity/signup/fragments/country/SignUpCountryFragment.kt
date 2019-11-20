package io.golos.cyber_android.ui.screens.login_activity.signup.fragments.country

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
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity.LoginActivityComponent
import io.golos.cyber_android.ui.common.base.FragmentBase
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.screens.login_activity.signup.SignUpViewModel
import io.golos.cyber_android.ui.utils.TextWatcherBase
import io.golos.domain.dto.CountryEntity
import kotlinx.android.synthetic.main.fragment_sign_up_country.*
import kotlinx.android.synthetic.main.view_search_bar.*
import javax.inject.Inject

/**
 * Fragment for selecting country (as [CountryModel]) for SignUpPhoneFragment
 */
class SignUpCountryFragment : FragmentBase() {

    private lateinit var viewModel: SignUpCountryViewModel

    private lateinit var signUpViewModel: SignUpViewModel

    @Inject
    internal lateinit var viewModelFactory: ActivityViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.injections.get<LoginActivityComponent>().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up_country, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ViewCompat.setElevation(searchBar, resources.getDimension(R.dimen.elevation_search_bar))
        val searchIcon = VectorDrawableCompat.create(resources, R.drawable.ic_search, null)
        searchBar.setCompoundDrawablesWithIntrinsicBounds(searchIcon, null, null, null)

        countriesList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        countriesList.adapter = CountriesAdapter(object : CountriesAdapter.Listener {
            override fun onCountryClick(country: CountryEntity) {
                signUpViewModel.onCountrySelected(country)
                findNavController().navigateUp()
            }
        })
        searchBar.addTextChangedListener(object : TextWatcherBase() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.makeSearch(s.toString())
            }
        })

        ivBack.setOnClickListener { findNavController().navigateUp() }

        setupViewModel()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.countries.observe(this, Observer {
            (countriesList.adapter as CountriesAdapter).submit(it)
        })

        viewModel.command.observe(this, Observer { command ->
            when(command) {
                is SetLoadingVisibilityCommand -> setLoadingVisibility(command.isVisible)
                is ShowMessageCommand -> uiHelper.showMessage(command.textResId)
                else -> throw UnsupportedOperationException("This command is not supported")
            }
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
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SignUpCountryViewModel::class.java)

        signUpViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(SignUpViewModel::class.java)
    }
}
