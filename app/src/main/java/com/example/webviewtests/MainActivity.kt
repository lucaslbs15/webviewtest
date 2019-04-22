package com.example.webviewtests

import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import android.widget.Toast
import okhttp3.*
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    lateinit var myPage: WebView

    //val MY_URL = "https://testsecureacceptance.cybersource.com/checkout"
    val MY_URL = "https://testsecureacceptance.cybersource.com/pay"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myPage = findViewById<WebView>(R.id.my_page)
        runOnUiThread {
            initWebViewWithPOST()
        }
    }

    private fun initWebView() {
        myPage.loadUrl(MY_URL, createHeaders())
        myPage.webViewClient = MyWebViewClient(this, "")
        myPage.visibility = View.VISIBLE
    }

    private fun initWebViewWithPOST() {
        val postDataBuilder = StringBuilder()
        postDataBuilder.append("unsigned_field_names:")
        postDataBuilder.appendln("amount:22190.69")
        postDataBuilder.appendln("bill_to_address_postal_code:91159777")
        postDataBuilder.appendln("submit:Submit")
        postDataBuilder.appendln("signature:lF7CVDVbxEZKW2LxsZlcpBbrI2Ll84EiCSI661777770=")
        postDataBuilder.appendln("bill_to_address_state:RS")
        postDataBuilder.appendln("transaction_uuid:f5b6f652-4deb-4e80-acd2-acc281860b5d")
        postDataBuilder.appendln("bill_to_email:joao.santos@gmail.com")
        postDataBuilder.appendln("locale:pt-BR")
        postDataBuilder.appendln("transaction_type:sale")
        postDataBuilder.appendln("signed_field_names:unsigned_field_names,amount,bill_to_address_postal_code,bill_to_address_state,transaction_uuid,bill_to_email,locale,transaction_type,reference_number,bill_to_address_country,bill_to_surname,bill_to_address_line1,profile_id,bill_to_phone,access_key,bill_to_address_city,currency,bill_to_forename,signed_date_time,payment_method,signed_field_names")
        postDataBuilder.appendln("reference_number:1554317354174")
        postDataBuilder.appendln("bill_to_address_country:BR")
        postDataBuilder.appendln("bill_to_surname:Nos")
        postDataBuilder.appendln("bill_to_address_line1:Joaquim Nabuco, 834")
        postDataBuilder.appendln("profile_id:65E35E01-C56E-4FA5-A822-6F7CA3D53162")
        postDataBuilder.appendln("bill_to_phone:123214142")
        postDataBuilder.appendln("access_key:edf5ec907ff53a878067f49eeda01c0f")
        postDataBuilder.appendln("bill_to_address_city:Canoas")
        postDataBuilder.appendln("currency:BRL")
        postDataBuilder.appendln("bill_to_forename:Fer1")
        postDataBuilder.appendln("signed_date_time:2019-04-03T18:49:14Z")
        postDataBuilder.appendln("payment_method:card")
        val postData = ""

        myPage.settings.javaScriptEnabled = true
        myPage.settings.domStorageEnabled = true
        myPage.settings.userAgentString = "Content-Type: application/x-www-form-urlencoded"
        //myPage.postUrl(MY_URL, postData.toByteArray(Charset.defaultCharset()))
        myPage.loadUrl(MY_URL)
        myPage.webViewClient = MyWebViewClient(this, postDataBuilder.toString())
        myPage.visibility = View.VISIBLE
    }

    /**
     * Passar como parâmetro no método loadUrl(String, HashMap) da WebView
     */
    fun createHeaders(): Map<String, String> {
        val headers: HashMap<String, String> = HashMap()
        headers.put("content-type", "application/x-www-form-urlencoded")
        return headers
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    class MyWebViewClient(val activity: AppCompatActivity, val body: String) : WebViewClient() {

        override fun shouldInterceptRequest(view: WebView?, url: String): WebResourceResponse? {
            Log.i("Bicca", "shouldInterceptRequest deprecated")

            val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; utf-8"), body)

            val httpClient = OkHttpClient()
            val request: Request = Request.Builder()
                .url(url.trim())
                .addHeader("Content-Type", "application/x-www-form-urlencoded; utf-8")
                .post(requestBody)
                .build()

            val response: Response = httpClient.newCall(request).execute()

            return WebResourceResponse(
                null, response.header("Content-Type", "application/x-www-form-urlencoded; utf-8"),
                response.body()?.byteStream())
        }

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest?): Boolean {
            // AQUI IDENTIFICA CADA URL ALTERADA.
            Log.i("Bicca", "shouldOverrideUrlLoading called. URL: ${view.url}")
            if (view.url.contains("/receipt")) {
                Toast.makeText(activity, "Pagamento efetuado com sucesso!", Toast.LENGTH_LONG).show()
                activity.finish()
            }
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            Log.i("Bicca", "onReceivedError called")
        }

        override fun onReceivedHttpError(
            view: WebView?,
            request: WebResourceRequest?,
            errorResponse: WebResourceResponse?
        ) {
            super.onReceivedHttpError(view, request, errorResponse)
            if (Build.VERSION.SDK_INT >= 21)
                Log.i("Bicca", "onReceivedHttpError called -> ${errorResponse?.statusCode}")
        }

        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            super.onReceivedSslError(view, handler, error)
            Log.i("Bicca", "onReceivedSslError called")
        }
    }
}
