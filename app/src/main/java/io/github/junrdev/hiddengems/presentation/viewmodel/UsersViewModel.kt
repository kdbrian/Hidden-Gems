package io.github.junrdev.hiddengems.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.junrdev.hiddengems.data.model.AccountDto
import io.github.junrdev.hiddengems.data.model.AppUser
import io.github.junrdev.hiddengems.data.model.GithubUser
import io.github.junrdev.hiddengems.data.model.UserAccount
import io.github.junrdev.hiddengems.data.repo.UsersRepo
import io.github.junrdev.hiddengems.util.Resource
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val usersRepo: UsersRepo
) : ViewModel() {

    private val _users: MutableLiveData<Resource<List<UserAccount>>> = MutableLiveData()
    val users: LiveData<Resource<List<UserAccount>>> get() = _users


    fun loginFirebaseUser(accountDto: AccountDto, onResource: (Resource<FirebaseUser>) -> Unit) {
        usersRepo.login(accountDto) { firebaseUserResource ->
            onResource(firebaseUserResource)
        }
    }

    fun signUpFirebaseUser(dto: AccountDto, onResource: (Resource<FirebaseUser>) -> Unit) {
        usersRepo.signUp(dto) { onResource(it) }
    }

    suspend fun loginGithubUserWithCode(code: String): Resource<String> { //returns the token
        val tokenResult = usersRepo.getGithubUserTokenFromLoginCode(code)
        return when (tokenResult.isSuccess) {
            true -> {
                Resource.Success(tokenResult.getOrNull()!!)
            }

            false -> {
                Resource.Error(message = tokenResult.exceptionOrNull()?.message.toString())
            }
        }
    }


    suspend fun saveGithubUser(token: String, onResource: (Resource<String>) -> Unit) {
        //get user info from token
        val infoResult = usersRepo.getGithubUserInfo(token)

        Timber.d("info res -> $infoResult")
        Timber.d("info ${infoResult.getOrNull()} err ${infoResult.exceptionOrNull()?.message}")

        when (infoResult.isSuccess) {
            true -> {
                //save details to firebase if not exists
                usersRepo.saveGithubUser(infoResult.getOrNull()!!) {
                    onResource(it)
                }
            }

            false -> {
                onResource(Resource.Error(message = infoResult.exceptionOrNull()?.message.toString()))
            }
        }
    }

    suspend fun loadUserDetails(
        uid: String,
        type: String,
        onResource: (Resource<out AppUser>) -> Unit
    ) {
        usersRepo.getUserDetails(uid, type, onResource)
    }


    fun toggleSetting(userId: String, type: String, remember: Boolean, location: Boolean) {
        usersRepo.toggleDefaultsInFirebase(userId, type, remember, location)
    }


    fun getAllUsers() {
        usersRepo.getAllUsers { _users.postValue(it) }
    }

}
