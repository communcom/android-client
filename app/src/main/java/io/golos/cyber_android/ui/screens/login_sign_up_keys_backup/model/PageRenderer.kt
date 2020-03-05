package io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.model

import android.content.Context
import android.graphics.Typeface
import android.text.Layout
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto.PdfPageData
import io.golos.cyber_android.ui.shared.extensions.getColorRes
import io.golos.pdf_renderer.document.DocumentPage
import io.golos.pdf_renderer.document_params.*
import io.golos.utils.scope
import java.text.SimpleDateFormat
import java.util.*

/**
 * Renders page with keys
 */
class PageRenderer(private val context: Context) {
    private val colorBlue = R.color.pdf_blue.getColor()
    private val colorBlueLight = R.color.pdf_blue_light.getColor()
    private val colorGrayDark = R.color.pdf_gray_dark.getColor()
    private val colorWhite = R.color.pdf_white.getColor()

    private val text14 = TextParams(size = 14f)
    private val text14Bold = text14.copy(typeFace = Typeface.BOLD)
    private val text14Gray = text14.copy(color = colorGrayDark)
    private val text20Bold = text14Bold.copy(size = 20f)
    private val text25Bold = TextParams(size = 25f, typeFace = Typeface.BOLD)


    @Suppress("NAME_SHADOWING")
    fun render(pdfPage: DocumentPage, sourceData: PdfPageData) {
        // Page
        scope(PageParams(margin = 25f)) { page ->
            // commun /
            pdfPage.drawTextLine(R.string.commun.getString(), TextLineParams(text14Bold, page.margin, page.margin))
            pdfPage.drawTextLine("/", TextLineParams(text25Bold.copy(color = colorBlue), page.margin+60, page.margin))

            // Created for
            pdfPage.drawTextLine(R.string.pdf_created_for.getString(), TextLineParams(text25Bold, page.margin, page.margin+50))

            // User name & date
            pdfPage.drawTextLine("@${sourceData.userName}", TextLineParams(text14Bold, page.margin, page.margin+80))
            pdfPage.drawTextLine(
                sourceData.createDate.getString(),
                TextLineParams(
                    text14Gray.copy(align = Layout.Alignment.ALIGN_OPPOSITE),
                    pdfPage.pageSize.width-page.margin,
                    page.margin+80))

            // Instruction
            pdfPage.drawTextParagraph(
                R.string.pdf_instruction.getString(),
                TextParagraphParams(text14, page.margin, page.margin+110, pdfPage.pageSize.width-page.margin*2)
            )

            // Central area with main data
            val dataArea = AreaParams(colorBlueLight, page.margin, page.margin+215, pdfPage.pageSize.width-page.margin*2, 395f, 12f)
            scope(dataArea) { area ->
                pdfPage.drawArea(area)

                val dataAreaMargin = 15f

                val fieldsGap = 10f
                val fieldsWidthLarge = area.width-dataAreaMargin*2
                val fieldsWidthSmall = (area.width-dataAreaMargin*2 - fieldsGap)/2
                val fieldsHeight = 55f
                val fieldMargin = 15f

                val fieldTemplate = AreaParams(
                    colorWhite,
                    area.left + dataAreaMargin,
                    area.top + dataAreaMargin,
                    fieldsWidthSmall,
                    fieldsHeight,
                    9f)
                val hintTemplate = TextLineParams(text14Gray, fieldMargin, fieldMargin)
                val textTemplate = TextLineParams(text14Bold, fieldMargin, fieldsHeight - fieldMargin - text14Gray.size * 0.2f)

                // Phone
                @Suppress("UnnecessaryVariable")
                val phoneField = fieldTemplate
                drawField(pdfPage, phoneField, hintTemplate, textTemplate, R.string.pdf_phone_number_label, sourceData.phoneNumber)

                // User Id
                val userIdField = phoneField.copy(left = phoneField.left + phoneField.width + fieldsGap)
                drawField(pdfPage, userIdField, hintTemplate, textTemplate, R.string.pdf_user_id_label, sourceData.userId)

                // Username
                val usernameField = phoneField.copy(width = fieldsWidthLarge, top = phoneField.top+phoneField.height+fieldsGap)
                drawField(pdfPage, usernameField, hintTemplate, textTemplate, R.string.pdf_username_label, sourceData.userName)

                // Password
                val passwordField = usernameField.copy(top = usernameField.top+usernameField.height+fieldsGap)
                drawField(pdfPage, passwordField, hintTemplate, textTemplate, R.string.pdf_password, sourceData.password)

                // Important note
                val noteParams = TextParagraphParams(
                    text14Gray.copy(align = Layout.Alignment.ALIGN_CENTER, size = 12f),
                    passwordField.left + passwordField.width/2,
                    passwordField.top+passwordField.height+fieldsGap,
                    area.width)
                pdfPage.drawTextParagraph(R.string.pdf_important_note.getString(), noteParams)

                // Owner
                val ownerField = passwordField.copy(top = area.top+area.height-dataAreaMargin-fieldsHeight)
                drawField(pdfPage, ownerField, hintTemplate, textTemplate, R.string.pdf_owner, sourceData.ownerKey)

                // Active
                val activeField = ownerField.copy(top = ownerField.top - fieldsGap - fieldsHeight)
                drawField(pdfPage, activeField, hintTemplate, textTemplate, R.string.pdf_active, sourceData.activeKey)
            }

            // Qr code
            val qrParams = QrParams(dataArea.left, dataArea.top+dataArea.height+15, 180f)
            pdfPage.drawQr(sourceData.qrText, qrParams)

            val left = qrParams.left + qrParams.size + 25

            // Setup code
            pdfPage.drawTextLine(R.string.pdf_setup_code.getString(), TextLineParams(text20Bold, left, qrParams.top + 20))

            // Scan code
            pdfPage.drawTextParagraph(
                R.string.pdf_scan_code.getString(),
                TextParagraphParams(text14, left, qrParams.top+40, pdfPage.pageSize.width))

            // Need help?
            pdfPage.drawTextLine(R.string.pdf_need_help.getString(), TextLineParams(text20Bold, left, qrParams.top + 130))

            // Email
            pdfPage.drawTextLine(
                R.string.pdf_support_email.getString(),
                TextLineParams(text14.copy(color = colorBlue), left, qrParams.top + 155))
        }
    }

    private fun drawField(
        pdfPage: DocumentPage,
        area: AreaParams,
        hintTemplate: TextLineParams,
        textTemplate: TextLineParams,
        @StringRes labelRes: Int,
        text: String) {

        scope(area) { phoneArea ->
            pdfPage.drawArea(phoneArea)

            pdfPage.drawTextLine(
                labelRes.getString(),
                hintTemplate.copy(left = hintTemplate.left+phoneArea.left, top = hintTemplate.top+phoneArea.top))

            pdfPage.drawTextLine(
                text,
                textTemplate.copy(left = textTemplate.left+phoneArea.left, top = textTemplate.top+phoneArea.top))
        }
    }

    @ColorInt
    private fun Int.getColor(): Int = context.resources.getColorRes(this)

    private fun Int.getString(): String = context.getString(this)

    private fun Date.getString(): String {
        val locale = Locale.getDefault()
        val suffixes = context.resources.getStringArray(R.array.pdf_day_end)

        val day = SimpleDateFormat("d", Locale.getDefault()).format(this)
        val daySuffix = (day.toInt() % 10).let { if(it in 1..3) suffixes[it-1] else suffixes[3] }
        val month = SimpleDateFormat("MMMM", locale).format(this)
        val year = SimpleDateFormat("yyyy", locale).format(this)

        return "${R.string.pdf_on.getString()} $month $day$daySuffix, $year"
    }
}

// Export example
//private fun export() {
//    val pageInfo = A4PageSize()
//
//    val pageData = PageData(
//        userName = "fasdscdsacsd3",
//        userId = "tst5utqchjpd",
//        createDate = Date(),
//        phoneNumber = "+19854344433",
//        password = "P5JPDqyFMs3cjHApwmCStBiCow6DLQRHABKo1GZNmNFQm92WGfHR",
//        activeKey = "5HqxtFTMWiiSF7eU5idX5Kujj5ggUa5tHiRVwAxHHuJLv7gUjFa",
//        ownerKey = "5K2QS5FTZ3rXSNyeXrgm443imZASVfUuQ36sXJBUnHr3nBRbb8M",
//        qrText = "Phew! We now know the basics of what StaticLayout is and how we can use it to draw multiline text to Canvas."
//    )
//
//    DocumentImpl.create(this, pageInfo).use { doc ->
//        doc.addPage().use { page ->
//            val renderer = PageRenderer(this)
//            renderer.render(page, pageData)
//        }
//
//        val file = File(selectedPath, "1.pdf")
//        FileOutputStream(file).use { stream ->
//            doc.writeTo(stream)
//        }
//    }
//}
