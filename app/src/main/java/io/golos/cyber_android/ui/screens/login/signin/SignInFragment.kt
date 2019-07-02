package io.golos.cyber_android.ui.screens.login.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.base.LoadingFragment
import io.golos.cyber_android.ui.common.extensions.reduceDragSensitivity
import io.golos.cyber_android.ui.screens.login.signin.qrCode.QrCodeSignInFragment
import io.golos.cyber_android.ui.screens.login.signin.qrCode.detector.QrCodeDecrypted
import io.golos.cyber_android.ui.screens.login.signin.userName.UserNameSignInFragment
import io.golos.cyber_android.views.utils.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_sign_in.*
import java.lang.UnsupportedOperationException


class SignInFragment : LoadingFragment(), SignInParentFragment {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewPager()
        setupTabLayout()

        back.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onQrCodeReceived(code: QrCodeDecrypted) {
        getChildTabFragment(SignInTab.LOGIN_KEY.index)!!.onQrCodeReceived(code)
        selectTab(SignInTab.LOGIN_KEY)
    }

    private fun setupViewPager() {
        signInPager.adapter = object : FragmentStateAdapter(childFragmentManager, this.lifecycle) {
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    SignInTab.SCAN_QR.index -> QrCodeSignInFragment.newInstance(SignInTab.SCAN_QR)
                    SignInTab.LOGIN_KEY.index -> UserNameSignInFragment.newInstance(SignInTab.LOGIN_KEY)
                    else -> throw UnsupportedOperationException("The tab is not supported")
                }
            }

            override fun getItemCount() = SignInTab.values().size
        }

        signInPager.reduceDragSensitivity()

        signInPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                getOtherChildTabFragment(position)?.onUnselected()
                getChildTabFragment(position)?.onSelected()

            }
        })

        signInPager.currentItem
    }

    private fun setupTabLayout() {
        TabLayoutMediator(signInTabs, signInPager) { tab, position ->
            tab.setText(SignInTab.values()[position].title)
        }.attach()

        selectTab(SignInTab.LOGIN_KEY)
    }

    private fun selectTab(tab: SignInTab) {
        tab.index.let {
            signInTabs.getTabAt(it)?.select()
            signInPager.setCurrentItem(it, false)
        }
    }

    private fun getChildTabFragment(position: Int): SignInChildFragment? =
        getChildTabFragment { it == SignInTab.fromIndex(position) }

    private fun getOtherChildTabFragment(position: Int): SignInChildFragment? =
        getChildTabFragment { it != SignInTab.fromIndex(position) }

    private fun getChildTabFragment(filterCondition: (SignInTab) ->  Boolean): SignInChildFragment? =
        childFragmentManager.fragments.firstOrNull {
            it is SignInChildFragment && filterCondition(it.tabCode)
        } as? SignInChildFragment
}
