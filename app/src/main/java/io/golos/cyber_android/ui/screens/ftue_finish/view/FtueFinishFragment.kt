package io.golos.cyber_android.ui.screens.ftue_finish.view

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.StyleSpan
import android.view.View
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentFtueFinishBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.screens.ftue_finish.di.FtueFinishFragmentComponent
import io.golos.cyber_android.ui.screens.ftue_finish.view.view_command.FtueFinishCommand
import io.golos.cyber_android.ui.screens.ftue_finish.view_model.FtueFinishViewModel
import kotlinx.android.synthetic.main.fragment_ftue_finish.*

class FtueFinishFragment : FragmentBaseMVVM<FragmentFtueFinishBinding, FtueFinishViewModel>(){

    override fun provideViewModelType(): Class<FtueFinishViewModel> = FtueFinishViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_ftue_finish

    override fun inject() = App.injections.get<FtueFinishFragmentComponent>()
        .inject(this)

    override fun releaseInjection() {
        App.injections.release<FtueFinishFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentFtueFinishBinding, viewModel: FtueFinishViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        finishText.text = getSpannableText()
        doneFinish.setOnClickListener {
            viewModel.onDoneClicked()
        }
    }

    override fun processViewCommand(command: ViewCommand) {
        super.processViewCommand(command)
        if(command is FtueFinishCommand){
            findNavController().navigate(command.navigationId)
        }
    }

    private fun getSpannableText(): Spannable {
        val ftueDescriptionStart = SpannableStringBuilder(getString(R.string.ftue_finish_description_1))
        ftueDescriptionStart.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            ftueDescriptionStart.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        val ftueDescriptionEnd = SpannableStringBuilder(getString(R.string.ftue_finish_description_2))
        return SpannableStringBuilder(TextUtils.concat(ftueDescriptionStart, ftueDescriptionEnd))
    }

    companion object {

        fun newInstance(): FtueFinishFragment = FtueFinishFragment()
    }
}