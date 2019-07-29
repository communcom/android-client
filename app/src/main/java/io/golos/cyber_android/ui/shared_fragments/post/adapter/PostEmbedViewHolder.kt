package io.golos.cyber_android.ui.shared_fragments.post.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.golos.domain.interactors.model.EmbedModel
import kotlinx.android.synthetic.main.item_content_embed.view.*


class PostEmbedViewHolder(val view: View) : RecyclerView.ViewHolder(view), LifecycleObserver {

    private var currentHtml = ""

    private var lifecycleOwner: LifecycleOwner? = null

    init {
        setupWebView()
    }

    fun bind(embedModel: EmbedModel, lifecycleOwner: LifecycleOwner) {

        Glide.with(view)
            .load(0)
            .into(view.embedImage)

        if (currentHtml.compareTo(embedModel.html) != 0 && embedModel.html.isNotBlank()) {
            view.webView.loadDataWithBaseURL(embedModel.url, embedModel.html, "text/html", "UTF-8", null)
            currentHtml = embedModel.html
            view.webView.visibility = View.GONE
            view.progressBar.visibility = View.VISIBLE
        }
        if (embedModel.html.isBlank() && embedModel.url.isNotBlank()) {
            view.webView.visibility = View.GONE
            view.progressBar.visibility = View.VISIBLE
            Glide.with(view)
                .load(embedModel.url)
                .fitCenter()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        view.progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        view.progressBar.visibility = View.GONE
                        return false
                    }
                })
                .into(view.embedImage)
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
                    return Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
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