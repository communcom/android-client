package io.golos.cyber_android.ui.screens.login_activity.license

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.base.FragmentBase
import kotlinx.android.synthetic.main.fragment_license.*

@Deprecated("")
class LicenseFragment : FragmentBase() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_license, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        licenseView.loadUrl("https://en.wikipedia.org/wiki/MIT_License#License_terms")
        licenseView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                acceptButton.isEnabled = true
                declineButton.isEnabled = true

                licenseLoading.visibility = View.INVISIBLE
                licenseView.visibility = View.VISIBLE
            }
        }

        acceptDeclineButtons.setOnCheckedChangeListener { group, checkedId ->
            nextButton.isEnabled = checkedId == R.id.acceptButton
        }

        nextButton.setOnClickListener {}
    }
}
