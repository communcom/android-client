package io.golos.cyber_android.ui.screens.post_report.view

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.DialogPostReportBinding
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.mvvm.DialogBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.post_report.di.PostReportFragmentComponent
import io.golos.cyber_android.ui.screens.post_report.view.view_commands.SendReportCommand
import io.golos.cyber_android.ui.screens.post_report.view_model.PostReportViewModel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.dialog_post_report.*

class PostReportDialog : DialogBaseMVVM<DialogPostReportBinding, PostReportViewModel>() {

    var onPostReportCompleteCallback: ((Report) -> Unit)? = null

    @Parcelize
    data class Args(
        val contentId: ContentId
    ) : Parcelable

    companion object {
        fun newInstance(args: Args): PostReportDialog {
            return PostReportDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(Tags.ARGS, args)
                }
            }
        }
    }

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
        onPostReportCompleteCallback = null
        removeListeners()
        dismiss()
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addListeners()
        btnSend.setOnClickListener {
            viewModel.onSendClicked()
        }
        ivClose.setOnClickListener {
            viewModel.onClosedClicked()
        }
    }

    override fun inject() {
        App.injections
            .get<PostReportFragmentComponent>(arguments!!.getParcelable<Args>(Tags.ARGS)!!.contentId)
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
            is SendReportCommand -> {
                onPostReportCompleteCallback?.invoke(command.report)
                dismiss()
            }
        }
    }

    override fun getTheme(): Int = R.style.PostFiltersBottomSheet

    private fun addListeners() {
        cbSpam.setOnCheckedChangeListener { _, _ ->
            viewModel.collectReason(Type.SPAM)
        }

        cbHarassment.setOnCheckedChangeListener { _, _ ->
            viewModel.collectReason(Type.HARASSMENT)
        }

        cbNiguty.setOnCheckedChangeListener { _, _ ->
            viewModel.collectReason(Type.NIGUTY)
        }

        cbViolence.setOnCheckedChangeListener { _, _ ->
            viewModel.collectReason(Type.VIOLENCE)
        }

        cbFalseNews.setOnCheckedChangeListener { _, _ ->
            viewModel.collectReason(Type.FALSENEWS)
        }

        cbTerrorism.setOnCheckedChangeListener { _, _ ->
            viewModel.collectReason(Type.TERRORISM)
        }

        cbHateSpeech.setOnCheckedChangeListener { _, _ ->
            viewModel.collectReason(Type.HATESPEECH)
        }

        cbUnauthorizedSales.setOnCheckedChangeListener { _, _ ->
            viewModel.collectReason(Type.UNAUTHORIZEDSALES)
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

    data class Report(val reasons: List<String>, val contentId: ContentId)

    enum class Type {
        SPAM,
        HARASSMENT,
        NIGUTY,
        VIOLENCE,
        FALSENEWS,
        TERRORISM,
        HATESPEECH,
        UNAUTHORIZEDSALES
    }
}