package io.golos.cyber_android.ui.screens.community_page_about

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.community_page_about.di.CommunityPageAboutFragmentComponent
import io.golos.cyber_android.databinding.FragmentCommunityPageAboutBinding
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import kotlinx.android.synthetic.main.fragment_community_page_about.*

class CommunityPageAboutFragment :
    FragmentBaseMVVM<FragmentCommunityPageAboutBinding, CommunityPageAboutViewModel>() {

    override fun releaseInjection() {
        App.injections.release<CommunityPageAboutFragmentComponent>()
    }

    override fun provideViewModelType(): Class<CommunityPageAboutViewModel> = CommunityPageAboutViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_community_page_about

    override fun inject() = App.injections
        .get<CommunityPageAboutFragmentComponent>(getDescription())
        .inject(this)

    override fun linkViewModel(binding: FragmentCommunityPageAboutBinding, viewModel: CommunityPageAboutViewModel) {
            binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.communityPageDescriptionLiveData.observe(this, Observer {
            if(TextUtils.isEmpty(it)){
                tvDescription.text = getString(R.string.missing_description)
            } else{
                tvDescription.text = it
            }
        })
        viewModel.start()
    }

    private fun getDescription(): String?{
        return arguments!!.getString(ARG_DESCRIPTION)
    }

    companion object {

        private const val ARG_DESCRIPTION = "ARG_DESCRIPTION"

        fun newInstance(description: String?): Fragment {
            val bundle = Bundle()
            bundle.putString(ARG_DESCRIPTION, description)
            val communityPageAboutFragment = CommunityPageAboutFragment()
            communityPageAboutFragment.arguments = bundle
            return communityPageAboutFragment
        }
    }
}