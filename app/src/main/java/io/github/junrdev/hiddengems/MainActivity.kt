package io.github.junrdev.hiddengems

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import cafe.adriel.voyager.navigator.Navigator
import com.jakeoils.android.presentation.ui.theme.HiddenGemsTheme
import timber.log.Timber


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleGithubAouth(intent)
        setContent {
            HiddenGemsTheme{
            }
        }
    }


    //recieve new intent
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleGithubAouth(intent)
    }

    //handle auth feedback from intent
    private fun handleGithubAouth(intent: Intent) {
        intent.data?.let { uri ->
            if (uri.scheme == "hiddengems" && uri.host == "hiddengemsghub0auth") {
                val code = uri.getQueryParameter("code")

                if (code != null) {

                    CoroutineScope(Dispatchers.Main).launch {
                        //login with code -> save data if not -> save uid -> proceed
                        val tokenResource = usersViewModel.loginGithubUserWithCode(code)
                    
                    }
                }
            }
        }
    }

    //get auth code from ghub
    private fun exchangeCodeForAccessToken(code: String) {

        val clientId = BuildConfig.clientID
        val clientSecret = BuildConfig.clientSecret

        val client = OkHttpClient()

        val body = FormBody.Builder()
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
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
                    val json = JSONObject(it)

                    CoroutineScope(Dispatchers.Main).launch {
                        if (json.has("error")) {
                            applicationContext.showToast(json.getString("error_description"))
                        } else {

                            val token = json.getString("access_token")
                            token.let {
//                                
                            }
                        }
                    }
                }
            }
        })


    }


}
