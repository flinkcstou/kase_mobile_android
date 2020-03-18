package kz.kase.terminal.ui.fragment

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import kz.kase.terminal.util.HtmlUtils

class WebViewFragment(val url: String = ""): Fragment() {



    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        Log.d("Fragment", "New copy fragment "+ this.javaClass.simpleName)
        val binding = kz.kase.terminal.databinding.FragmentWebViewBinding.inflate(inflater, container, false)
        var url = arguments?.getString("url", "")
        if (url == null)
           url = this.url
        val client = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }

            @TargetApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView, url: String) {
                binding.progress.visibility =View.GONE
            }
        }
        binding.webView.webViewClient = client
        HtmlUtils.prepareWebView(binding.webView)
        binding.webView.loadUrl(url)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val tabLayout = activity?.findViewById(R.id.tabs) as TabLayout
//        if (tabLayout != null) {
//            tabLayout.visibility = View.VISIBLE
//            //tabLayout!!.setupWithViewPager(viewPager)
//        }
    }

    override fun onResume() {
        super.onResume()

    }

}
