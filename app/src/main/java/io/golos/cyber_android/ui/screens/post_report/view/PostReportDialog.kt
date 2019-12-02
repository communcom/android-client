package io.golos.cyber_android.ui.screens.post_report.view

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.DialogPostReportBinding
import io.golos.cyber_android.ui.common.mvvm.DialogBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.screens.post_report.PostReportHolder
import io.golos.cyber_android.ui.screens.post_report.di.PostReportFragmentComponent
import io.golos.cyber_android.ui.screens.post_report.view_model.PostReportViewModel
import kotlinx.android.synthetic.main.dialog_post_report.*

class PostReportDialog : DialogBaseMVVM<DialogPostReportBinding, PostReportViewModel>() {

    override fun layoutResId(): Int = R.layout.dialog_post_report

    override fun provideViewModelType(): Class<PostReportViewModel> = PostReportViewModel::class.java

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        return BottomSheetDialog(requireContext(), theme)
    }

    override fun onStart() {
        super.onStart()
        changeBottomSheetBehaviorState(BottomSheetBehavior.STATE_EXPANDED)
    }

    override fun onResume() {
        super.onResume()
        setBottomSheetBahavioutStateListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeListeners()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addListeners()
        observeViewModel()
        btnSend.setOnClickListener {
            viewModel.onSendClicked()
        }
        ivClose.setOnClickListener {
            viewModel.onClosedClicked()
        }
    }

    override fun inject() {
        App.injections
            .get<PostReportFragmentComponent>()
            .inject(this)
    }

    override fun releaseInjection() {
        App.injections.release<PostReportFragmentComponent>()
    }

    override fun linkViewModel(binding: DialogPostReportBinding, viewModel: PostReportViewModel) {
        binding.viewModel = viewModel
    }

    override fun processViewCommand(command: ViewCommand) {
        super.processViewCommand(command)
        when (command) {
            is BackCommand -> dismiss()
        }
    }

    override fun getTheme(): Int = R.style.PostFiltersBottomSheet

    private fun observeViewModel() {
        viewModel.reportType.observe(this, Observer { type ->

        })
    }

    private fun addListeners() {
        cbSpam.setOnCheckedChangeListener { _, _ ->
            viewModel.collectReport(PostReportHolder.Type.SPAM)
        }

        cbHarassment.setOnCheckedChangeListener { _, _ ->
            viewModel.collectReport(PostReportHolder.Type.HARASSMENT)
        }

        cbNiguty.setOnCheckedChangeListener { _, _ ->
            viewModel.collectReport(PostReportHolder.Type.NIGUTY)
        }

        cbViolence.setOnCheckedChangeListener { _, _ ->
            viewModel.collectReport(PostReportHolder.Type.VIOLENCE)
        }

        cbFalseNews.setOnCheckedChangeListener { _, _ ->
            viewModel.collectReport(PostReportHolder.Type.FALSENEWS)
        }

        cbTerrorism.setOnCheckedChangeListener { _, _ ->
            viewModel.collectReport(PostReportHolder.Type.TERRORISM)
        }

        cbHateSpeech.setOnCheckedChangeListener { _, _ ->
            viewModel.collectReport(PostReportHolder.Type.HATESPEECH)
        }

        cbUnauthorizedSales.setOnCheckedChangeListener { _, _ ->
            viewModel.collectReport(PostReportHolder.Type.UNAUTHORIZEDSALES)
        }
    }

    private fun removeListeners() {
        cbSpam.setOnCheckedChangeListener(null)
        cbHarassment.setOnCheckedChangeListener(null)
        cbNiguty.setOnCheckedChangeListener(null)
        cbViolence.setOnCheckedChangeListener(null)
        cbFalseNews.setOnCheckedChangeListener(null)
        cbTerrorism.setOnCheckedChangeListener(null)
        cbHateSpeech.setOnCheckedChangeListener(null)
        cbUnauthorizedSales.setOnCheckedChangeListener(null)
    }

    private fun getBehaviour(): BottomSheetBehavior<View>? {
        val dialog = dialog
        if (dialog != null) {
            val viewById = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            if (viewById != null) {
                val bottomSheet = viewById as FrameLayout
                return BottomSheetBehavior.from(bottomSheet)
            }
        }
        return null
    }

    private fun setBottomSheetBahavioutStateListener() {
        val behaviour = getBehaviour()
        behaviour?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    viewModel.onClosedClicked()
                }
            }

        })
    }

    private fun changeBottomSheetBehaviorState(state: Int) {
        val behaviour = getBehaviour()
        behaviour?.state = state
    }
}