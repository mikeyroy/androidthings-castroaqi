package us.mikepearce.castroaqi

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.TextView
import us.mikepearce.castroaqi.R.id.webview



class MainActivity : Activity() {
    private lateinit var myWebView: WebView
    private lateinit var offlineView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myWebView = findViewById<WebView>(R.id.webview) as WebView
        offlineView = findViewById<TextView>(R.id.offline) as TextView

        myWebView.settings.javaScriptEnabled = true

        myWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        myWebView.settings.domStorageEnabled = true;
        myWebView.webChromeClient = (object : WebChromeClient() {
            override fun onJsPrompt(
                view: WebView?,
                url: String?,
                message: String?,
                defaultValue: String?,
                result: JsPromptResult?
            ): Boolean {
                return super.onJsPrompt(view, url, message, defaultValue, result)
            }
            override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                //Required functionality here
                return super.onJsAlert(view, url, message, result)
            }
        })

        loadWebView()
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    private var broadcastReceiver: BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
            if (notConnected) {
                disconnected()
            } else {
                loadWebView()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    private fun loadWebView(): Unit {
        if (isNetworkAvailable()) {
            myWebView.visibility = View.VISIBLE
            offlineView.visibility = View.INVISIBLE

            myWebView.loadUrl("https://www.roypearce.us/aqi")
        } else {
            disconnected()
        }
    }

    private fun disconnected(): Unit {
        myWebView.visibility = View.INVISIBLE
        offlineView.visibility = View.VISIBLE
    }
}
