package io.golos.cyber_android.ui.screens.app_start.sign_up.countries.view

import android.os.Bundle
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentSignUpCountryBinding
import io.golos.cyber_android.ui.screens.app_start.sign_up.countries.di.SignUpCountryComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.countries.view.list.CountriesAdapter
import io.golos.cyber_android.ui.screens.app_start.sign_up.countries.view_model.SignUpCountryViewModel
import io.golos.cyber_android.ui.shared.extensions.setTextChangeListener
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import kotlinx.android.synthetic.main.fragment_sign_up_country.*
import kotlinx.android.synthetic.main.view_search_bar_pure.*

class SignUpCountryFragment : FragmentBaseMVVM<FragmentSignUpCountryBinding, SignUpCountryViewModel>() {

    private var searchTextWatcher: TextWatcher? = null

    override fun provideViewModelType(): Class<SignUpCountryViewModel> = SignUpCountryViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_sign_up_country

    override fun inject(key: String) = App.injections.get<SignUpCountryComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignUpCountryComponent>(key)

    override fun linkViewModel(binding: FragmentSignUpCountryBinding, viewModel: SignUpCountryViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        countriesList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        countriesList.adapter = CountriesAdapter { viewModel.onCountrySelected(it) }

        searchTextWatcher = searchBar.setTextChangeListener { viewModel.makeSearch(it) }

        header.setOnBackButtonClickListener { findNavController().navigateUp() }

        viewModel.countries.observe(viewLifecycleOwner, Observer {
            (countriesList.adapter as CountriesAdapter).submit(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchTextWatcher?.let { searchBar.removeTextChangedListener(it) }
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateBackwardCommand -> findNavController().navigateUp()
        }
    }
}
