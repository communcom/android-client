package io.golos.cyber_android.ui.screens.notifications.view

import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentNotificationsBinding
import io.golos.cyber_android.ui.screens.feed_my.di.MyFeedFragmentComponent
import io.golos.cyber_android.ui.screens.notifications.di.NotificationsFragmentComponent
import io.golos.cyber_android.ui.screens.notifications.view_model.NotificationsViewModel
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM

class NotificationsFragment : FragmentBaseMVVM<FragmentNotificationsBinding, NotificationsViewModel>() {

    override fun provideViewModelType(): Class<NotificationsViewModel> = NotificationsViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_notifications

    override fun inject(key: String) = App.injections.get<NotificationsFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<MyFeedFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentNotificationsBinding, viewModel: NotificationsViewModel) {
        binding.viewModel = viewModel
    }

    companion object {
        fun newInstance() = NotificationsFragment()
    }


}
