package io.golos.cyber_android.ui.screens.post_filters

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_filters.PostFiltersFragmentComponent
import io.golos.cyber_android.databinding.DialogPostFiltersBinding
import io.golos.cyber_android.ui.common.mvvm.DialogBaseMVVM

class PostFiltersBottomSheetDialog : DialogBaseMVVM<DialogPostFiltersBinding, PostFiltersViewModel>() {

    override fun provideViewModelType(): Class<PostFiltersViewModel> = PostFiltersViewModel::class.java

    override fun layoutResId(): Int = R.layout.dialog_post_filters

    override fun inject() {
        App.injections
            .get<PostFiltersFragmentComponent>()
            .inject(this)
    }

    override fun releaseInjection() {
        App.injections.release<PostFiltersFragmentComponent>()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        return BottomSheetDialog(requireContext())
    }

    override fun linkViewModel(binding: DialogPostFiltersBinding, viewModel: PostFiltersViewModel) {
        binding.viewModel = viewModel
    }

}