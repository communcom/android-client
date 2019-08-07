package io.golos.cyber_android.ui.screens.feedback_activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.feedback_activity.FeedbackActivityComponent
import io.golos.domain.CrashlyticsFacade
import kotlinx.android.synthetic.main.activity_feedback.*
import javax.inject.Inject

class FeedbackActivity : AppCompatActivity() {
    companion object {
        private var isRunning = false

        fun start(context: Context) {
            if(isRunning) {
                return
            }

            Intent(context, FeedbackActivity::class.java)
                .let {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(it)
                }
        }
    }

    @Inject
    internal lateinit var crashlytics: CrashlyticsFacade

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isRunning = true

        App.injections.get<FeedbackActivityComponent>().inject(this)

        setContentView(R.layout.activity_feedback)

        sendButton.setOnClickListener {
            crashlytics.sendReport(requestText.text.toString())
            finish()
        }

        cancelButton.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()

        if(isFinishing) {
            App.injections.release<FeedbackActivityComponent>()
            isRunning = false
        }
    }
}
