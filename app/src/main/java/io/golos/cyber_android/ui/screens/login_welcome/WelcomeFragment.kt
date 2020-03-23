package io.golos.cyber_android.ui.screens.login_welcome


import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.shared.analytics.AnalyticsFacade
import io.golos.cyber_android.ui.screens.login_activity.di.LoginActivityComponent
import io.golos.cyber_android.ui.shared.base.FragmentBaseCoroutines
import io.golos.cyber_android.ui.shared.utils.ViewUtils
import kotlinx.android.synthetic.main.fragment_welcome.*
import javax.inject.Inject

class WelcomeFragment : FragmentBaseCoroutines() {

    @Inject
    internal lateinit var analyticsFacade: AnalyticsFacade

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.injections.getBase<LoginActivityComponent>().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,  savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val slides = listOf(
            SlideItem(
                R.drawable.img_welcome_one,
                getFirstSlideHeaderText(),
                getFirstSlideText()
            ),
            SlideItem(
                R.drawable.img_welcome_two,
                getSecondSlideHeaderText(),
                getSecondSlideText()
            ),
//            SlideItem(
//                R.drawable.img_welcome_three,
//                getThirdSlideText(),
//                SpannableStringBuilder(getString(R.string.welcome_slides_sub_text_3))
//            ),
            SlideItem(
                R.drawable.img_welcome_four,
                getFourthSlideHeaderText(),
                getFourthSlideText()
            )
        )
        setupSlidesPager(slides)

        topSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_signInFragment)
        }
        signIn.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_signInFragment)
        }
        signUp.setOnClickListener {
            analyticsFacade.clickGetStarted031()
            findNavController().navigate(R.id.action_welcomeFragment_to_signUpSelectMethod)
        }
        nextSlide.setOnClickListener {
            slidesPager.setCurrentItem(slidesPager.currentItem + 1, true)
        }
    }

    private fun setupSlidesPager(slides: List<SlideItem>) {
        slidesPager.adapter = WelcomeSlidesAdapter(slides)
        dotsIndicator.setViewPager2(slidesPager)

        slidesPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when(position) {
                    0 -> analyticsFacade.openScreen011()
                    1 -> analyticsFacade.openScreen012()
                    2 -> analyticsFacade.openScreen013()
                }

                val lastItem = slides.size - 1
                if (lastItem == position) {
                    signIn.visibility = View.VISIBLE
                    signUp.visibility = View.VISIBLE
                    nextSlide.visibility = View.GONE
                } else {
                    signIn.visibility = View.GONE
                    signUp.visibility = View.GONE
                    nextSlide.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun getFirstSlideHeaderText(): Spannable {
        val firstSlideTextBegin = SpannableStringBuilder(getString(R.string.welcome_slides_text_0_0))
        val firstSlideTextEnd = SpannableStringBuilder(getString(R.string.welcome_slides_text_0_1))
        val blueColor = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.blue))
        firstSlideTextEnd.setSpan(
            blueColor,
            3,
            firstSlideTextEnd.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        firstSlideTextEnd.setSpan(
            StyleSpan(BOLD),
            3,
            firstSlideTextEnd.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return SpannableStringBuilder(TextUtils.concat(firstSlideTextBegin, firstSlideTextEnd))
    }

    private fun getFirstSlideText(): Spannable {
        val text = SpannableStringBuilder(getString(R.string.welcome_slides_sub_text_1))

        val blueColor = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.blue))
        val blackColor = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.black))

        text.setSpan(blackColor, 0, text.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        text.setSpan(blueColor, 45, 53, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        return text
    }

    private fun getSecondSlideHeaderText(): Spannable {
        val secondSlideTextBegin = SpannableStringBuilder(getString(R.string.welcome_slides_text_1))
        secondSlideTextBegin.setSpan(
            StyleSpan(BOLD),
            0,
            secondSlideTextBegin.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        val secondSlideTextEnd = SpannableStringBuilder(getString(R.string.welcome_slides_text_1_1))
        return SpannableStringBuilder(TextUtils.concat(secondSlideTextBegin, secondSlideTextEnd))
    }

    private fun getSecondSlideText(): Spannable {
        val text = SpannableStringBuilder(getString(R.string.welcome_slides_sub_text_2))

        val blueColor = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.blue))
        val blackColor = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.black))

        text.setSpan(blackColor, 0, text.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        text.setSpan(blueColor, 38, 47, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        return text
    }

//    private fun getThirdSlideText(): Spannable {
//        val thirdSlideTextBegin = SpannableStringBuilder(getString(R.string.welcome_slides_text_2))
//        thirdSlideTextBegin.setSpan(
//            StyleSpan(BOLD),
//            0,
//            thirdSlideTextBegin.length,
//            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
//        )
//        val thirdSlideTextEnd = SpannableStringBuilder(getString(R.string.welcome_slides_text_2_1))
//        return SpannableStringBuilder(TextUtils.concat(thirdSlideTextBegin, thirdSlideTextEnd))
//    }

    private fun getFourthSlideHeaderText(): Spannable {
        val fourthSlideTextBegin = SpannableStringBuilder(getString(R.string.welcome_slides_text_3))
        fourthSlideTextBegin.setSpan(
            StyleSpan(BOLD),
            0,
            fourthSlideTextBegin.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        val fourthSlideTextEnd = SpannableStringBuilder(getString(R.string.welcome_slides_text_3_1))
        return SpannableStringBuilder(TextUtils.concat(fourthSlideTextBegin, " ", fourthSlideTextEnd))
    }

    private fun getFourthSlideText(): Spannable {
        val text = SpannableStringBuilder(getString(R.string.welcome_slides_sub_text_4))

        val blackColor = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.black))

        text.setSpan(blackColor, 0, text.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        return text
    }

    override fun onResume() {
        super.onResume()
        ViewUtils.hideKeyboard(requireActivity())
    }
}

