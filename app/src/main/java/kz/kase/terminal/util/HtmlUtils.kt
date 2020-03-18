package kz.kase.terminal.util

import android.graphics.Color
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView

class HtmlUtils {
    companion object {
        fun destroyWebView(webView: WebView?) {
            if (webView != null) {
                val parent = webView.parent as ViewGroup
                parent?.removeView(webView)
                webView.removeAllViews()
                webView.destroy()
            }
        }

        fun prepareWebView(mWebView: WebView) {
            mWebView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            mWebView.settings.loadsImagesAutomatically = true
            mWebView.settings.displayZoomControls = false
            mWebView.settings.builtInZoomControls = false
            mWebView.settings.javaScriptEnabled = true
            mWebView.settings.domStorageEnabled = true
            mWebView.settings.allowFileAccess = false
            if (Build.VERSION.SDK_INT > 16) {
                mWebView.settings.mediaPlaybackRequiresUserGesture = false // for ad
            }
            mWebView.settings.loadWithOverviewMode = true
            mWebView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
            mWebView.isScrollbarFadingEnabled = false
            mWebView.webChromeClient = WebChromeClient()
            mWebView.setBackgroundColor(Color.argb(1, 0, 0, 0)) //TRANSPARENT
            if (Build.VERSION.SDK_INT >= 19) {
                mWebView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
            }
        }
    }
}