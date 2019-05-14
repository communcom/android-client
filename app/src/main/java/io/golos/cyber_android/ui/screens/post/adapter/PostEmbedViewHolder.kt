package io.golos.cyber_android.ui.screens.post.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import io.golos.domain.interactors.model.EmbedModel
import kotlinx.android.synthetic.main.item_content_embed.view.*


class PostEmbedViewHolder(val view: View) : RecyclerView.ViewHolder(view), LifecycleObserver {

    private var currentHtml = ""

    private var lifecycleOwner: LifecycleOwner? = null

    init {
        setupWebView()
    }

    fun bind(embedModel: EmbedModel, lifecycleOwner: LifecycleOwner) {
        if (currentHtml.compareTo(embedModel.html) != 0) {
            view.webView.loadDataWithBaseURL(embedModel.url, embedModel.html, "text/html", "UTF-8", null)
            currentHtml = embedModel.html
            view.webView.visibility = View.GONE
            view.progressBar.visibility = View.VISIBLE
        }
        this.lifecycleOwner?.lifecycle?.removeObserver(this)
        this.lifecycleOwner = lifecycleOwner
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private fun setupWebView() {
        with(view.webView) {
            @SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            //workaround for "bitmap.getWidth()" exception
            webChromeClient = object : WebChromeClient() {
                override fun getDefaultVideoPoster(): Bitmap? {
                    return android.graphics.Bitmap.createBitmap(50, 50, android.graphics.Bitmap.Config.ARGB_8888)
                }
            }
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    onPageLoaded()
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    return true
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    return true
                }
            }
        }
    }

    private fun onPageLoaded() {
        view.webView.visibility = View.VISIBLE
        view.progressBar.visibility = View.GONE
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        view.webView.onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        view.webView.onResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        view.webView.onDestroy()
    }


}