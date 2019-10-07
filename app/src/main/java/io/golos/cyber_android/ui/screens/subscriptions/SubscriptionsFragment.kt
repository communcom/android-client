package io.golos.cyber_android.ui.screens.subscriptions

import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.subscriptions.SubscriptionsFragmentComponent
import io.golos.cyber_android.databinding.FragmentSubscriptionsBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM

class SubscriptionsFragment : FragmentBaseMVVM<FragmentSubscriptionsBinding, SubscriptionsModel, SubscriptionsViewModel>() {


    override fun provideViewModelType(): Class<SubscriptionsViewModel> = SubscriptionsViewModel::class.java

    override fun provideLayout(): Int = R.layout.fragment_subscriptions

    override fun inject() = App.injections
        .get<SubscriptionsFragmentComponent>()
        .inject(this)

    override fun linkViewModel(binding: FragmentSubscriptionsBinding, viewModel: SubscriptionsViewModel) {
        binding.viewModel = viewModel
    }
}
