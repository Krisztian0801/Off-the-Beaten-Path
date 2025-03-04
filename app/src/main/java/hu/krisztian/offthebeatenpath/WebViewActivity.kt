package hu.krisztian.offthebeatenpath

import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        // Toolbar setup
        val toolbar: Toolbar = findViewById(R.id.webViewToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val webView: WebView = findViewById(R.id.webView)
        val pdfUrl = intent.getStringExtra("pdf_url")

        if (pdfUrl != null) {
            webView.settings.javaScriptEnabled = true
            webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            webView.settings.setSupportZoom(true)
            webView.settings.builtInZoomControls = true
            webView.settings.displayZoomControls = false
            webView.webViewClient = WebViewClient()

            //val googleDocsViewer = "https://docs.google.com/gview?embedded=true&url=$pdfUrl"
            //webView.loadUrl(googleDocsViewer)
            webView.loadUrl(pdfUrl)
        }
    }

    // Handle toolbar back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
