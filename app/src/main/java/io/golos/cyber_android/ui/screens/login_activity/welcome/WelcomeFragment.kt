package io.golos.cyber_android.ui.screens.login_activity.welcome


import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.NavigationArgs
import io.golos.cyber_android.ui.base.FragmentBase
import io.golos.cyber_android.views.utils.ViewUtils
import kotlinx.android.synthetic.main.fragment_welcome.*
import java.text.MessageFormat
import java.util.concurrent.TimeUnit

class WelcomeFragment : FragmentBase() {
    private val startAnimDelay = TimeUnit.SECONDS.toMillis(7)
    private val userDragAnimDelay = TimeUnit.SECONDS.toMillis(7)
    private val animDuration = TimeUnit.SECONDS.toMillis(3)

    private var handler: Handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val slides = listOf(
            SlideItem(R.drawable.img_welcome_0, getFirstSlideText(), Gravity.END),
            SlideItem(R.drawable.img_welcome_1, SpannableStringBuilder(getString(R.string.welcome_slides_text_1)), Gravity.CENTER),
            SlideItem(R.drawable.img_welcome_2, SpannableStringBuilder(getString(R.string.welcome_slides_text_2)), Gravity.CENTER)
        )
        setupSlidesPager(slides)

        signIn.setOnClickListener {
            val args = Bundle().also { it.putString(NavigationArgs.DESTINATION, NavigationArgs.SIGN_IN) }
            findNavController().navigate(R.id.action_welcomeFragment_to_licenseFragment, args)
        }
        signUp.setOnClickListener {
            val args = Bundle().also { it.putString(NavigationArgs.DESTINATION, NavigationArgs.SIGN_UP) }
            findNavController().navigate(R.id.action_welcomeFragment_to_licenseFragment, args)
        }

        versionText.text =
            MessageFormat.format(resources.getString(R.string.display_version), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
    }

    private fun setupSlidesPager(slides: List<SlideItem>) {
        slidesPager.adapter = WelcomeSlidesAdapter(slides)
        slidesIndicator.setupWithViewPager(slidesPager, slides.size)

        slidesPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                    if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                        handler.removeCallbacks(switchSlideRunnable)
                        handler.postDelayed(switchSlideRunnable, userDragAnimDelay)
                    }
            }
        })
    }

    private fun getFirstSlideText(): Spannable {
        val firstSlideTextBegin = SpannableStringBuilder(getString(R.string.welcome_slides_text_0_0))
        val blackColor = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.black))
        firstSlideTextBegin.setSpan(blackColor, 0, firstSlideTextBegin.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        val firstSlideTextEnd = SpannableStringBuilder(getString(R.string.welcome_slides_text_0_1))
        val blueColor = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.blue))
        firstSlideTextEnd.setSpan(blueColor, 0, firstSlideTextEnd.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        return SpannableStringBuilder(TextUtils.concat(firstSlideTextBegin, " ", firstSlideTextEnd))
    }

    private val switchSlideRunnable = object : Runnable {
        override fun run() {
            handler.postDelayed(this, animDuration)
            slidesPager.setCurrentItem(slidesPager.currentItem + 1, true)
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(switchSlideRunnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(switchSlideRunnable, startAnimDelay)
        ViewUtils.hideKeyboard(requireActivity())
    }
}

