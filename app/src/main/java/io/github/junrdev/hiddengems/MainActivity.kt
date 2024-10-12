package io.github.junrdev.hiddengems

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.presentation.ui.AppDatastore
import io.github.junrdev.hiddengems.presentation.ui.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var appDatastore: AppDatastore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //recieve new intent
    override fun onNewIntent(intent: Intent) {
        handleGithubAouth(intent)
        super.onNewIntent(intent)
    }

    //handle auth feedback from intent
    private fun handleGithubAouth(intent: Intent) {
        intent.data?.let { uri ->
            if (uri.scheme == "hiddengems" && uri.host == "hiddengemsghub0auth") {
                val code = uri.getQueryParameter("code")

                if (code != null) {
                    println(
                        "code $code"
                    )

                    exchangeCodeForAccessToken(code)
                }
            }


        }
    }

    //get auth code from ghub
    private fun exchangeCodeForAccessToken(code: String) {

//        val clientId = BuildConfig.
        val client = OkHttpClient()

        val body = FormBody.Builder()
            .add("client_id", "")
            .add("client_secret", "")
            .add("code", code)
            .build()

        val req = Request.Builder()
            .url("https://github.com/login/oauth/access_token")
            .post(body)
            .header("Accept", "application/json")
            .build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                applicationContext.showToast("Failed due to ${e.message.toString()}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.also {
                    println(
                        "res(login) $it"
                    )
                    val json = JSONObject(it)
                    val token = json.getString("access_token")
                    token.let {
                        CoroutineScope(Dispatchers.Main).launch {
                            appDatastore.loginGhubUser(token)
                        }
                    }
                }
            }
        })


    }


}