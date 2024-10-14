package io.github.junrdev.hiddengems.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.junrdev.hiddengems.data.model.AccountDto
import io.github.junrdev.hiddengems.data.model.GithubUser
import io.github.junrdev.hiddengems.data.model.UserAccount
import io.github.junrdev.hiddengems.data.repo.UsersRepo
import io.github.junrdev.hiddengems.util.Resource
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val usersRepo: UsersRepo
) : ViewModel() {

    private val _users: MutableLiveData<Resource<List<UserAccount>>> = MutableLiveData()
    val users: LiveData<Resource<List<UserAccount>>> get() = _users

    private val _loggedInState: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val loggedInState: LiveData<Resource<Boolean>> get() = _loggedInState

    fun loginFirebaseUser(accountDto: AccountDto, onResource: (Resource<FirebaseUser>) -> Unit) {
        usersRepo.login(accountDto) { firebaseUserResource ->
            onResource(firebaseUserResource)
        }
    }

    fun signUpFirebaseUser(dto: AccountDto, onResource: (Resource<FirebaseUser>) -> Unit) {
        usersRepo.signUp(dto) { onResource(it) }
    }

    fun getAllUsers() {
        usersRepo.getAllUsers { _users.postValue(it) }
    }


    suspend fun loginGithubUserWithCode(code: String): Resource<String> { //returns the token
        _loggedInState.postValue(Resource.Loading())
        val tokenResult = usersRepo.getGithubUserTokenFromLoginCode(code)
        return when (tokenResult.isSuccess) {
            true -> {
                _loggedInState.postValue(Resource.Success(true))
                Resource.Success(tokenResult.getOrNull()!!)
            }

            false -> {
                _loggedInState.postValue(Resource.Error(message = tokenResult.exceptionOrNull()?.message.toString()))
                Resource.Error(message = tokenResult.exceptionOrNull()?.message.toString())
            }
        }
    }

    suspend fun saveGithubUser(token: String, onResource: (Resource<String>) -> Unit) {
        //get user info from token
        val infoResult = usersRepo.getGithubUserInfo(token)

        println("info $infoResult")
        println("info ${infoResult.getOrNull()} err ${infoResult.exceptionOrNull()?.message}")

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


    suspend fun loadGithubUserDetails(uid: String, onResource: (Resource<GithubUser>) -> Unit) {
        usersRepo.getGithubUserDetails(uid, onResource)
    }

}