package io.golos.cyber_android.ui.screens.community_page_rules

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page_rules.CommunityPageRulesFragmentComponent
import io.golos.cyber_android.databinding.FragmentCommunityPageRulesBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import kotlinx.android.synthetic.main.fragment_community_page_rules.*

class CommunityPageRulesFragment :
    FragmentBaseMVVM<FragmentCommunityPageRulesBinding, CommunityPageRulesViewModel>() {

    override fun releaseInjection() {
        App.injections.release<CommunityPageRulesFragmentComponent>()
    }

    override fun provideViewModelType(): Class<CommunityPageRulesViewModel> = CommunityPageRulesViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_community_page_rules

    override fun inject() = App.injections
        .get<CommunityPageRulesFragmentComponent>(getRules())
        .inject(this)

    override fun linkViewModel(binding: FragmentCommunityPageRulesBinding, viewModel: CommunityPageRulesViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.communityPageRulesLiveData.observe(this, Observer {
            if (TextUtils.isEmpty(it)) {
                tvRules.text = getString(R.string.missing_description)
            } else {
                tvRules.text = it
            }
        })
        viewModel.start()
    }

    private fun getRules(): String? {
        return arguments!!.getString(ARG_RULES)
    }

    companion object {

        private const val ARG_RULES = "ARG_RULES"

        fun newInstance(description: String?): Fragment {
            val bundle = Bundle()
            bundle.putString(ARG_RULES, description)
            val communityPageRulesFragment = CommunityPageRulesFragment()
            communityPageRulesFragment.arguments = bundle
            return communityPageRulesFragment
        }
    }
}