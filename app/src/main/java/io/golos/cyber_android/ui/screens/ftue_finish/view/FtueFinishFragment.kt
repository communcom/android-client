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
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigationCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.screens.ftue_finish.di.FtueFinishFragmentComponent
import io.golos.cyber_android.ui.screens.ftue_finish.view_model.FtueFinishViewModel
import io.golos.cyber_android.ui.shared.utils.navigate
import kotlinx.android.synthetic.main.fragment_ftue_finish.*

class FtueFinishFragment : FragmentBaseMVVM<FragmentFtueFinishBinding, FtueFinishViewModel>(){

    override fun provideViewModelType(): Class<FtueFinishViewModel> = FtueFinishViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_ftue_finish

    override fun inject(key: String) = App.injections.get<FtueFinishFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<FtueFinishFragmentComponent>(key)

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
        if(command is NavigationCommand){
            findNavController().navigate(command)
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