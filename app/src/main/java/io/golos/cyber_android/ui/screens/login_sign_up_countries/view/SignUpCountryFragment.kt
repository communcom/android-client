package io.golos.cyber_android.ui.screens.login_sign_up_countries.view

import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentSignUpCountryBinding
import io.golos.cyber_android.ui.screens.login_sign_up_countries.di.SignUpCountryComponent
import io.golos.cyber_android.ui.screens.login_sign_up_countries.view.list.CountriesAdapter
import io.golos.cyber_android.ui.screens.login_sign_up_countries.view_model.SignUpCountryViewModel
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared.utils.TextWatcherBase
import kotlinx.android.synthetic.main.fragment_sign_up_country.*
import kotlinx.android.synthetic.main.view_search_bar_pure.*

class SignUpCountryFragment : FragmentBaseMVVM<FragmentSignUpCountryBinding, SignUpCountryViewModel>() {

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

        searchBar.addTextChangedListener(object : TextWatcherBase() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.makeSearch(s.toString())
            }
        })

        ivBack.setOnClickListener { findNavController().navigateUp() }

        viewModel.countries.observe(viewLifecycleOwner, Observer {
            (countriesList.adapter as CountriesAdapter).submit(it)
        })
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateBackwardCommand -> findNavController().navigateUp()
        }
    }
}
