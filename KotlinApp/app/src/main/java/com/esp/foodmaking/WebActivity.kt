package com.esp.foodmaking

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_web.*

class WebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        initViews()
    }

    private fun initViews() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
        val publisher = intent.getBooleanExtra("publisher", false)
        val food = intent.getSerializableExtra("food") as Food
        if (publisher) {
            webView.webViewClient = WebviewClient(food.publisherUrl)
            webView.webChromeClient = WebClient(progressBar)
            webView.settings.loadsImagesAutomatically = true
            toolbarTitle.text = food.publisher
            webView.loadUrl(food.publisherUrl)

        } else {
            webView.webViewClient = WebviewClient(food.sourceUrl)
            webView.webChromeClient = WebClient(progressBar)
            webView.settings.loadsImagesAutomatically = true
            toolbarTitle.text = food.title
            webView.loadUrl(food.sourceUrl)
        }
    }
}
