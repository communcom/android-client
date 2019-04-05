package io.golos.cyber_android.ui.screens.editor

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.base.LoadingFragment
import io.golos.cyber_android.views.utils.BaseTextWatcher
import io.golos.cyber_android.views.utils.colorizeHashTags
import io.golos.cyber_android.views.utils.colorizeLinks
import io.golos.domain.interactors.model.CommunityModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.LinkEmbedModel
import io.golos.domain.model.QueryResult
import kotlinx.android.synthetic.main.fragment_editor_page.*

const val GALLERY_REQUEST = 101

const val TITLE_MAX_LENGTH = 256

class EditorPageFragment : LoadingFragment() {

    data class Args(
        val type: EditorPageViewModel.Type,
        val parentDiscussionId: DiscussionIdModel? = null,
        val community: CommunityModel? = null
    )

    private lateinit var viewModel: EditorPageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_editor_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        observeViewModel()

        close.setOnClickListener { activity?.finish() }
        post.setOnClickListener { viewModel.post() }

        title.addTextChangedListener(object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                viewModel.onTitleChanged(s.toString())
            }
        })

        content.addTextChangedListener(object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.onContentChanged(s.toString())
                s?.colorizeHashTags()
                s?.colorizeLinks()
            }
        })

        title.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
        title.filters = arrayOf(InputFilter.LengthFilter(TITLE_MAX_LENGTH))

        nsfw.setOnClickListener {
            viewModel.switchNSFW()
        }

        photo.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            )
            intent.type = "image/*"
            startActivityForResult(intent, GALLERY_REQUEST)
        }

        with(linkPreviewWebView) {
            @SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
            //workaround for "bitmap.getWidth()" exception
            webChromeClient = object : WebChromeClient() {
                override fun getDefaultVideoPoster(): Bitmap? {
                    return Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
                }
            }
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    showPreviewLayout()
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    return true
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    return true
                }
            }
        }

        setupFragmentType(getArgs().type)
    }

    private fun setupCommunity(community: CommunityModel) {
        communityName.text = community.name
        Glide.with(this)
            .load(community.avatarUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(communityAvatar)
    }

    /**
     * Sets the views states according to [EditorPageViewModel.Type]
     */
    private fun setupFragmentType(type: EditorPageViewModel.Type) {
        title.visibility = if (type == EditorPageViewModel.Type.COMMENT) View.GONE else View.VISIBLE
        toolbarTitle.setText(if (type == EditorPageViewModel.Type.COMMENT) R.string.create_comment else R.string.create_post)
    }

    private fun observeViewModel() {
        viewModel.validationResultLiveData.observe(this, Observer {
            post.isEnabled = it
            post.alpha = if (it) 1f else 0.3f
        })

        viewModel.embedLiveDate.observe(this, Observer { result ->
            when (result) {
                is QueryResult.Loading -> onEmbedLoading()
                is QueryResult.Success -> onEmbedResult(result.originalQuery)
                is QueryResult.Error -> onEmbedError()
            }
        })

        viewModel.emptyEmbedLiveData.observe(this, Observer { isEmpty ->
            if (isEmpty)
                hidePreviewLayout()
        })

        viewModel.discussionCreationLiveData.observe(this, Observer { event ->
            event.getIfNotHandled()?.let { result ->
                when (result) {
                    is QueryResult.Loading<*> -> onPostLoading()
                    is QueryResult.Success<*> -> onPostResult()
                    is QueryResult.Error<*> -> onPostError()
                }
            }
        })

        viewModel.nsfwLiveData.observe(this, Observer {
            nsfw.isActivated = it
        })

        viewModel.communityLiveData.observe(this, Observer {
            if (it != null)
                setupCommunity(it)
        })
    }

    private fun onPostError() {
        Toast.makeText(requireContext(), "Post creation error", Toast.LENGTH_SHORT).show()
        hideLoading()
    }

    private fun onPostResult() {
        Toast.makeText(requireContext(), "Post creation success", Toast.LENGTH_SHORT).show()
        hideLoading()
        activity?.finish()
    }

    private fun onPostLoading() {
        showLoading()
    }

    private fun onEmbedResult(model: LinkEmbedModel) {
        previewSummary.text = model.summary
        previewProvider.text = model.provider
        previewSummary.visibility = if (model.summary.isBlank()) View.GONE else View.VISIBLE
        previewProvider.visibility = if (model.provider.isBlank()) View.GONE else View.VISIBLE

        if (model.embedHtml.isNotBlank()) {
            loadEmbeddedHtml(model)
        } else {
            if (model.thumbnailImageUrl.isNotBlank()) {
                loadThumbnail(model)
            }
        }
    }

    private fun loadThumbnail(model: LinkEmbedModel) {
        linkPreviewWebView.visibility = View.GONE
        linkPreviewImageView.visibility = View.VISIBLE


        Glide.with(requireContext())
            .load(model.thumbnailImageUrl)
            .fitCenter()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    linkPreviewLayout.post {
                        showPreviewLayout()
                        linkPreviewImageView.setImageDrawable(resource)
                    }
                    return false
                }
            })
            .submit()
    }

    private fun loadEmbeddedHtml(model: LinkEmbedModel) {
        linkPreviewWebView.visibility = View.VISIBLE
        linkPreviewImageView.visibility = View.GONE
        linkPreviewWebView.loadData(model.embedHtml, "text/html", "UTF-8")
    }

    private fun onEmbedError() {
        hidePreviewLayout()
    }

    private fun onEmbedLoading() {
        linkPreviewProgress.visibility = View.VISIBLE
        linkPreviewWebView.visibility = View.GONE
        linkPreviewImageView.visibility = View.GONE
    }

    private fun hidePreviewLayout() {
        linkPreviewLayout.visibility = View.GONE
    }

    private fun showPreviewLayout() {
        linkPreviewLayout.visibility = View.VISIBLE
        linkPreviewProgress.visibility = View.GONE
    }

    private fun setupViewModel() {
        val args = getArgs()
        viewModel = ViewModelProviders.of(
            this,
            requireActivity()
                .serviceLocator
                .getEditorPageViewModelFactory(args.type, args.parentDiscussionId, args.community)
        ).get(EditorPageViewModel::class.java)
    }

    private fun getArgs() = requireContext()
        .serviceLocator
        .moshi
        .adapter(Args::class.java)
        .fromJson(arguments!!.getString(Tags.ARGS)!!)!!

    override fun onPause() {
        linkPreviewWebView.onPause()
        super.onPause()
    }

    override fun onResume() {
        linkPreviewWebView.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        linkPreviewWebView?.onDestroy()
        super.onDestroy()
    }
}
