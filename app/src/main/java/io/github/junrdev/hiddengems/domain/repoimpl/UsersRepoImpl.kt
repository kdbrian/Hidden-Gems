package io.github.junrdev.hiddengems.domain.repoimpl

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import io.github.junrdev.hiddengems.BuildConfig
import io.github.junrdev.hiddengems.data.model.AccountDto
import io.github.junrdev.hiddengems.data.model.GithubUser
import io.github.junrdev.hiddengems.data.model.UserAccount
import io.github.junrdev.hiddengems.data.repo.UsersRepo
import io.github.junrdev.hiddengems.util.Constant
import io.github.junrdev.hiddengems.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject

private const val TAG = "UsersRepoImpl"

class UsersRepoImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UsersRepo {

    override fun signUp(dto: AccountDto, onResource: (Resource<FirebaseUser>) -> Unit) {
        onResource(Resource.Loading())
        firebaseAuth.createUserWithEmailAndPassword(dto.email, dto.password)
            .addOnSuccessListener { authResult ->
                println("result ${authResult.user}")
                authResult.user?.let { firebaseUser ->
                    firestore.collection(Constant.usercollection)
                        .document(firebaseUser.uid)
                        .set(UserAccount(uid = firebaseUser.uid, email = firebaseUser.email!!))
                        .addOnSuccessListener { onResource(Resource.Success(data = firebaseUser)) }
                        .addOnFailureListener { onResource(Resource.Error(message = it.message.toString())) }
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "signUp: ${it.message.toString()}")
                onResource(Resource.Error(message = it.localizedMessage.toString()))
            }
    }

    override fun login(dto: AccountDto, onResource: (Resource<FirebaseUser>) -> Unit) {
        onResource(Resource.Loading())
        firebaseAuth.signInWithEmailAndPassword(dto.email, dto.password)
            .addOnSuccessListener { authResult ->
                authResult.user?.let {
                    onResource(Resource.Success(data = it))
                }
            }
            .addOnFailureListener { onResource(Resource.Error(message = it.message.toString())) }
    }

    override fun getAllUsers(onResource: (Resource<List<UserAccount>>) -> Unit) {
        val users = firestore.collection(Constant.usercollection)
        users.get()
            .addOnSuccessListener {
                val accounts = it.toObjects(UserAccount::class.java)
                onResource(Resource.Success(data = accounts))
            }
            .addOnFailureListener { onResource(Resource.Error(message = it.message.toString())) }
    }

    override fun saveGithubUser(githubUser: GithubUser, onResource: (Resource<String>) -> Unit) {
        firestore.collection(Constant.githubuserscollection)
            //check for user
            .whereEqualTo("uid", githubUser.uid)
            .get()
            .addOnSuccessListener {
                val users = it.toObjects(GithubUser::class.java)
                if (users.isNotEmpty()) {
                    //user already exists : return firebase Id
                    val user = users.first()
                    user?.let { guser ->
                        onResource(Resource.Success(guser.id!!))
                    }
                } else {
                    //first timer
                    val userRef = firestore.collection(Constant.githubuserscollection).document()
                    userRef.set(
                        githubUser.copy(id = userRef.id)
                    ).addOnSuccessListener {
                        onResource(Resource.Success(userRef.id))
                    }.addOnFailureListener { onResource(Resource.Error(it.message.toString())) }
                }
            }

    }

    override suspend fun getGithubUserTokenFromLoginCode(code: String): Result<String> {
        val client = OkHttpClient()
        val clientId = BuildConfig.clientID
        val clientSecret = BuildConfig.clientSecret

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

        return try {
            val resp = CoroutineScope(Dispatchers.IO).async {
                client.newCall(req).execute()
            }.await()

            if (resp.isSuccessful) {
                val respBody = resp.body!!.string()
                val json = JSONObject(respBody)
                println("json $json")
                if (json.has("error")) {
                    Result.failure(Exception(json.getString("error_description")))
                } else {
                    val token = json.getString("access_token")
                    Result.success(token)
                }

            } else {
                println("error ${resp.message}")
                Result.failure(Exception(resp.message))
            }
        } catch (e: Exception) {
            println("errorcatch ${e.message}")
            println(e)
            Result.failure(e)
        }
    }

    override suspend fun getGithubUserInfo(token: String): Result<GithubUser> {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.github.com/user")
            .header("Authorization", "Bearer $token")
            .build()
        return try {
            val resp = CoroutineScope(Dispatchers.IO).async {
                client.newCall(request).execute()
            }.await()


            if (resp.isSuccessful) {
                val userData = resp.body!!.string()
                println("userdate $userData")

                val json = JSONObject(userData)
                Result.success(
                    GithubUser(
                        username = json.getString("login"),
                        uid = json.getString("id"),
                        followers = json.getString("followers"),
                        avatarUrl = json.getString("avatar_url"),
                    )
                )
            } else
                Result.failure(Exception(resp.message))
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun getGithubUserDetails(
        uid: String,
        onResource: (Resource<GithubUser>) -> Unit
    ) {
        firestore.collection(Constant.githubuserscollection)
            .document(uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val accs = documentSnapshot.toObject(GithubUser::class.java)
                accs?.let { onResource(Resource.Success(it)) }
            }.addOnFailureListener { onResource(Resource.Error(it.message.toString())) }
    }

}