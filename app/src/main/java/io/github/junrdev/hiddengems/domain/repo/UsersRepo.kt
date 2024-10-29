package io.github.junrdev.hiddengems.domain.repo

import com.google.firebase.auth.FirebaseUser
import io.github.junrdev.hiddengems.data.model.AccountDto
import io.github.junrdev.hiddengems.data.model.AppUser
import io.github.junrdev.hiddengems.data.model.GithubUserAccount
import io.github.junrdev.hiddengems.data.model.FirebaseUserAccount
import io.github.junrdev.hiddengems.util.Resource

interface UsersRepo {

    fun signUp(dto: AccountDto, onResource: (Resource<FirebaseUser>) -> Unit)
    fun login(dto: AccountDto, onResource: (Resource<FirebaseUser>) -> Unit)
    fun getAllUsers(onResource: (Resource<List<FirebaseUserAccount>>) -> Unit)

    //return firebase result(uid)
    fun saveGithubUser(githubUserAccount: GithubUserAccount, onResource: (Resource<String>) -> Unit)

    suspend fun getGithubUserTokenFromLoginCode(code: String): Result<String>

    suspend fun getGithubUserInfo(token: String): Result<GithubUserAccount>

    suspend fun getUserDetails(
        uid: String,
        account: String,
        onResource: (Resource<out AppUser>) -> Unit
    )

    fun toggleDefaultsInFirebase(
        userId: String,
        accountType: String,
        rememberUser: Boolean,
        locationSharing: Boolean
    )
}