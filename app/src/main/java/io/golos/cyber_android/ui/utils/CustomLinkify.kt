package io.golos.cyber_android.ui.utils

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.URLSpan
import android.text.util.Linkify
import android.util.Log
import android.widget.TextView
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


private val EMPTY_STRING = emptyArray<String>()

/**
 * Replacement for [Linkify] that doesn't reapply spans if there is already appropriate ones.
 */
object CustomLinkify {

    /**
     * Applies a regex to the text of a TextView turning the matches into
     * links.  If links are found then UrlSpans are applied to the link
     * text match areas, and the movement method for the text is changed
     * to LinkMovementMethod.
     *
     * @param text         TextView whose text is to be marked-up with links
     * @param pattern      Regex pattern to be used for finding links
     * @param scheme       URL scheme string (eg `http://`) to be
     * prepended to the links that do not start with this scheme.
     */
    fun addLinks(
        text: TextView,
        pattern: Pattern,
        scheme: String?
    ) {
        addLinks(text, pattern, scheme, null, null, null)
    }

    /**
     * Applies a regex to the text of a TextView turning the matches into
     * links.  If links are found then UrlSpans are applied to the link
     * text match areas, and the movement method for the text is changed
     * to LinkMovementMethod.
     *
     * @param text TextView whose text is to be marked-up with links.
     * @param pattern Regex pattern to be used for finding links.
     * @param defaultScheme The default scheme to be prepended to links if the link does not
     * start with one of the `schemes` given.
     * @param schemes Array of schemes (eg `http://`) to check if the link found
     * contains a scheme. Passing a null or empty value means prepend defaultScheme
     * to all links.
     * @param matchFilter  The filter that is used to allow the client code additional control
     * over which pattern matches are to be converted into links.
     * @param transformFilter Filter to allow the client code to update the link found.
     */
    private fun addLinks(
        text: TextView, pattern: Pattern,
        defaultScheme: String?, schemes: Array<String>?,
        matchFilter: Linkify.MatchFilter?, transformFilter: Linkify.TransformFilter?
    ) {
        val spannable = SpannableString.valueOf(text.text)

        val linksAdded = addLinks(
            spannable, pattern, defaultScheme, schemes, matchFilter,
            transformFilter
        )
        if (linksAdded) {
            text.text = spannable
        }
    }

    /**
     *  Applies a regex to a Spannable turning the matches into
     *  links.
     *
     *  @param text         Spannable whose text is to be marked-up with links
     *  @param pattern      Regex pattern to be used for finding links
     *  @param scheme       URL scheme string (eg <code>http://</code>) to be
     *                      prepended to the links that do not start with this scheme.
     */
    fun addLinks(
        text: Spannable, pattern: Pattern,
        scheme: String?
    ): Boolean {
        return addLinks(text, pattern, scheme, null, null, null)
    }

    /**
     * Applies a regex to a Spannable turning the matches into links.
     *
     * @param spannable Spannable whose text is to be marked-up with links.
     * @param pattern Regex pattern to be used for finding links.
     * @param defaultSchemePar The default scheme to be prepended to links if the link does not
     *                      start with one of the <code>schemes</code> given.
     * @param schemesPar Array of schemes (eg <code>http://</code>) to check if the link found
     *                contains a scheme. Passing a null or empty value means prepend defaultScheme
     *                to all links.
     * @param matchFilter  The filter that is used to allow the client code additional control
     *                     over which pattern matches are to be converted into links.
     * @param transformFilter Filter to allow the client code to update the link found.
     *
     * @return True if at least one link is found and applied.
     */
    private fun addLinks(
        spannable: Spannable, pattern: Pattern,
        defaultSchemePar: String?, schemesPar: Array<String>?,
        matchFilter: Linkify.MatchFilter?, transformFilter: Linkify.TransformFilter?
    ): Boolean {
        var defaultScheme = defaultSchemePar
        var schemes = schemesPar

        val schemesCopy: Array<String>
        if (defaultScheme == null) defaultScheme = ""
        if (schemes == null || schemes.isEmpty()) {
            schemes = EMPTY_STRING
        }

        schemesCopy = Array(schemes.size + 1) { "" }
        schemesCopy[0] = defaultScheme.toLowerCase(Locale.ROOT)
        for (index in schemes.indices) {
            val scheme = schemes[index]
            schemesCopy[index + 1] = scheme.toLowerCase(Locale.ROOT)
        }

        var hasMatches = false
        val m = pattern.matcher(spannable)

        while (m.find()) {
            val start = m.start()
            val end = m.end()

            var allowed = true

            if (matchFilter != null) {
                allowed = matchFilter.acceptMatch(spannable, start, end)
            }

            if (allowed) {
                val url = makeUrl(m.group(0), schemesCopy, m, transformFilter)

                //if there is an appropriate span already then skip this match
                if (spannable
                        .getSpans(start, end, URLSpanNoUnderline::class.java)
                        .find { it.url.compareTo(url) == 0 } != null
                ) {
                    Log.i("CustomLinkify", "skip span")
                    continue
                }

                applyLink(url, start, end, spannable)
                hasMatches = true
            }
        }

        return hasMatches
    }

    private fun makeUrl(
        urlPar: String, prefixes: Array<String>,
        matcher: Matcher, filter: Linkify.TransformFilter?
    ): String {
        var url = urlPar
        if (filter != null) {
            url = filter.transformUrl(matcher, url)
        }

        var hasPrefix = false

        for (i in prefixes.indices) {
            if (url.regionMatches(0, prefixes[i], 0, prefixes[i].length, ignoreCase = true)) {
                hasPrefix = true

                // Fix capitalization if necessary
                if (!url.regionMatches(0, prefixes[i], 0, prefixes[i].length, ignoreCase = false)) {
                    url = prefixes[i] + url.substring(prefixes[i].length)
                }

                break
            }
        }

        if (!hasPrefix && prefixes.isNotEmpty()) {
            url = prefixes[0] + url
        }

        return url
    }

    private fun applyLink(url: String, start: Int, end: Int, text: Spannable) {
        val span = URLSpanNoUnderline(url)
        text.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        Log.i("CustomLinkify", "apply span")
    }

    /**
     * Subclass of [URLSpan] that doesn't draw underline
     */
    private class URLSpanNoUnderline(url: String) : URLSpan(url) {
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }

}

