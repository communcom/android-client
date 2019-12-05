package io.golos.cyber_android.ui.screens.ftue.view

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentFtueBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.screens.ftue.di.FtueFragmentComponent
import io.golos.cyber_android.ui.screens.ftue.viewmodel.FtueViewModel
import kotlinx.android.synthetic.main.fragment_ftue.*

class FtueFragment : FragmentBaseMVVM<FragmentFtueBinding, FtueViewModel>() {

    override fun provideViewModelType(): Class<FtueViewModel> = FtueViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_ftue

    override fun inject() = App.injections.get<FtueFragmentComponent>()
        .inject(this)

    override fun releaseInjection() {
        App.injections.release<FtueFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentFtueBinding, viewModel: FtueViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCommunUrlStyles()
    }

    private fun setCommunUrlStyles(){
        val commun = SpannableStringBuilder(getString(R.string.commun))
        val slash = SpannableStringBuilder(" /")
        val blueColor = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.blue))
        commun.setSpan(
            blueColor,
            0,
            commun.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        slash.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            slash.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        tvCommunUrl.text = SpannableStringBuilder(TextUtils.concat(commun, slash))
    }
}