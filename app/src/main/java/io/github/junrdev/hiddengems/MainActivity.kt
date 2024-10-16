package io.github.junrdev.hiddengems

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.databinding.ActivityMainBinding
import io.github.junrdev.hiddengems.presentation.ui.AppDatastore
import io.github.junrdev.hiddengems.presentation.ui.showToast
import io.github.junrdev.hiddengems.presentation.viewmodel.UsersViewModel
import io.github.junrdev.hiddengems.util.Resource
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

    lateinit var binding: ActivityMainBinding
    private val usersViewModel by viewModels<UsersViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        handleGithubAouth(intent)
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
                        when (tokenResource) {
                            is Resource.Error -> applicationContext.showToast(tokenResource.message.toString())
                            is Resource.Loading -> Unit
                            is Resource.Success -> {
                                //we have the token
                                usersViewModel.saveGithubUser(tokenResource.data!!) { userIdResource ->
                                    when (userIdResource) {
                                        is Resource.Error -> applicationContext.showToast(
                                            userIdResource.message.toString()
                                        )

                                        is Resource.Loading -> Unit

                                        is Resource.Success -> {
                                            //user viewmodel to save user info
                                            CoroutineScope(Dispatchers.Main).launch {
                                                appDatastore.loginGhubUser(
                                                    tokenResource.data,
                                                    userIdResource.data!!
                                                )
                                                val navController =
                                                    binding.fragmentContainerView.findNavController()
                                                navController.navigate(R.id.homeScreen)

                                            }
                                        }
                                    }
                                }
                            }

                        }
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
//                                appDatastore.loginGhubUser(token)
                                //user viewmodel to save user info
                                val navController =
                                    binding.fragmentContainerView.findNavController()
                                navController.navigate(R.id.appnavigation)
                            }
                        }
                    }
                }
            }
        })


    }


}