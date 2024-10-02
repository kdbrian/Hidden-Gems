package io.github.junrdev.hiddengems.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.junrdev.hiddengems.data.model.AccountDto
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

    fun loginUser(accountDto: AccountDto, onResource: (Resource<FirebaseUser>) -> Unit) {
        usersRepo.login(accountDto) { firebaseUserResource ->
            onResource(firebaseUserResource)
        }
    }

    fun signUpUser(dto: AccountDto, onResource: (Resource<FirebaseUser>) -> Unit) {
        usersRepo.signUp(dto) { onResource(it) }
    }

    fun getAllUsers() {
        usersRepo.getAllUsers { _users.postValue(it) }
    }

}